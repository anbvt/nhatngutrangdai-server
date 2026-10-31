package jp.co.nhatngutrangdai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;
import java.security.SecureRandom;
import java.time.Instant;

import jp.co.nhatngutrangdai.auth.entity.Role;
import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.auth.repository.RoleRepository;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(SchoolManagementBackendApplicationTests.TestApiConfiguration.class)
class SchoolManagementBackendApplicationTests {

	private static final String BOOTSTRAP_EMAIL = "bootstrap-" + UUID.randomUUID() + "@example.test";
	private static final String BOOTSTRAP_PASSWORD = UUID.randomUUID().toString();
	private static final String JWT_SECRET = createJwtSecret();

	@LocalServerPort
	private int port;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UserAccountRepository userAccountRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JwtDecoder jwtDecoder;

	@DynamicPropertySource
	static void securityProperties(DynamicPropertyRegistry registry) {
		registry.add("app.security.jwt.secret", () -> JWT_SECRET);
		registry.add("app.security.jwt.access-token-ttl", () -> "15m");
		registry.add("app.security.bootstrap-super-admin.email", () -> BOOTSTRAP_EMAIL);
		registry.add("app.security.bootstrap-super-admin.password", () -> BOOTSTRAP_PASSWORD);
	}

	@Test
	void healthEndpointIsAvailableWithoutExposingDetails() throws Exception {
		HttpResponse<String> response = sendRequest("/actuator/health");

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("\"status\":\"UP\""));
	}

	@Test
	void unknownRouteUsesStandardNotFoundError() throws Exception {
		HttpResponse<String> response = sendRequest("/missing-route");

		assertEquals(404, response.statusCode());
		assertTrue(response.body().contains("\"code\":\"NOT_FOUND\""));
	}

	@Test
	void openApiDocumentIsAvailable() throws Exception {
		HttpResponse<String> response = sendRequest("/api-docs");

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("\"openapi\""));
	}

	@Test
	void versionedMigrationRunsOnAnEmptyDatabase() {
		Long appliedFoundationMigration = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '1'",
				Long.class);
		Long appliedAuthMigration = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '2'",
				Long.class);

		assertEquals(1L, appliedFoundationMigration);
		assertEquals(1L, appliedAuthMigration);
	}

	@Test
	void bootstrapCreatesSuperAdminWithHashedPassword() {
		UserAccount account = userAccountRepository.findByEmailIgnoreCase(BOOTSTRAP_EMAIL).orElseThrow();

		assertTrue(passwordEncoder.matches(BOOTSTRAP_PASSWORD, account.getPasswordHash()));
		assertTrue(account.getRoles().stream().anyMatch(role -> role.getCode() == RoleCode.SUPER_ADMIN));
		assertTrue(!account.getPasswordHash().equals(BOOTSTRAP_PASSWORD));
	}

	@Test
	void loginReturnsBearerTokenAndMeReturnsCurrentUser() throws Exception {
		HttpResponse<String> loginResponse = postLogin(BOOTSTRAP_EMAIL, BOOTSTRAP_PASSWORD);
		assertEquals(200, loginResponse.statusCode());

		var body = objectMapper.readTree(loginResponse.body());
		String token = body.path("accessToken").asText();
		assertTrue(!token.isBlank());
		assertEquals("Bearer", body.path("tokenType").asText());
		assertEquals(BOOTSTRAP_EMAIL, body.path("user").path("email").asText());
		Instant expiration = jwtDecoder.decode(token).getExpiresAt();
		assertTrue(expiration.isAfter(Instant.now().plusSeconds(14 * 60L)));
		assertTrue(expiration.isBefore(Instant.now().plusSeconds(16 * 60L)));

		HttpResponse<String> meResponse = sendRequest("/api/v1/auth/me", token);
		assertEquals(200, meResponse.statusCode());
		assertEquals(BOOTSTRAP_EMAIL, objectMapper.readTree(meResponse.body()).path("email").asText());
	}

	@Test
	void unauthenticatedMeRequestUsesStandard401Response() throws Exception {
		HttpResponse<String> response = sendRequest("/api/v1/auth/me");

		assertEquals(401, response.statusCode());
		assertEquals("UNAUTHENTICATED", objectMapper.readTree(response.body()).path("code").asText());
	}

	@Test
	void invalidCredentialsDoNotRevealWhetherEmailExists() throws Exception {
		HttpResponse<String> existing = postLogin(BOOTSTRAP_EMAIL, "incorrect-password");
		HttpResponse<String> missing = postLogin(UUID.randomUUID() + "@example.test", "incorrect-password");

		assertEquals(401, existing.statusCode());
		assertEquals(401, missing.statusCode());
		assertEquals(
				objectMapper.readTree(existing.body()).path("code").asText(),
				objectMapper.readTree(missing.body()).path("code").asText());
	}

	@Test
	void passwordLongerThanBcryptLimitReturnsUnauthorized() throws Exception {
		HttpResponse<String> response = postLogin(
				BOOTSTRAP_EMAIL,
				"é".repeat(37));

		assertEquals(401, response.statusCode());
		assertEquals("INVALID_CREDENTIALS", objectMapper.readTree(response.body()).path("code").asText());
	}

	@Test
	void studentCannotAccessAdminEndpoint() throws Exception {
		String email = UUID.randomUUID() + "@example.test";
		String password = UUID.randomUUID().toString();
		String token = createUserAndLogin(email, password, RoleCode.STUDENT);

		HttpResponse<String> response = sendRequest("/api/v1/admin/test-access", token);

		assertEquals(403, response.statusCode());
		assertEquals("FORBIDDEN", objectMapper.readTree(response.body()).path("code").asText());
	}

	@Test
	void studentCannotAccessAdminStudentList() throws Exception {
		String email = UUID.randomUUID() + "@example.test";
		String token = createUserAndLogin(email, UUID.randomUUID().toString(), RoleCode.STUDENT);

		HttpResponse<String> response = sendRequest("/api/v1/students/test-admin-list", token);

		assertEquals(403, response.statusCode());
	}

	@Test
	void adminCanAccessAdminStudentList() throws Exception {
		HttpResponse<String> response = sendRequest("/api/v1/students/test-admin-list", bootstrapToken());

		assertEquals(200, response.statusCode());
	}

	@Test
	void adminCanAccessAdminEndpoint() throws Exception {
		HttpResponse<String> response = sendRequest("/api/v1/admin/test-access", bootstrapToken());

		assertEquals(200, response.statusCode());
	}

	@Test
	void unconfiguredApiRoutesRemainDenied() throws Exception {
		HttpResponse<String> response = sendRequest("/api/v1/unconfigured-resource", bootstrapToken());

		assertEquals(403, response.statusCode());
	}

	@Test
	void studentCannotAccessAnotherUsersResource() throws Exception {
		String password = UUID.randomUUID().toString();
		String ownerEmail = UUID.randomUUID() + "@example.test";
		String otherEmail = UUID.randomUUID() + "@example.test";
		String ownerToken = createUserAndLogin(ownerEmail, password, RoleCode.STUDENT);
		String otherToken = createUserAndLogin(otherEmail, password, RoleCode.STUDENT);
		UUID ownerId = userAccountRepository.findByEmailIgnoreCase(ownerEmail).orElseThrow().getId();

		HttpResponse<String> ownResource = sendRequest(
				"/api/v1/students/me/" + ownerId + "/test-owned-resource",
				ownerToken);
		HttpResponse<String> anotherResource = sendRequest(
				"/api/v1/students/me/" + ownerId + "/test-owned-resource",
				otherToken);
		assertEquals(200, ownResource.statusCode());
		assertEquals(403, anotherResource.statusCode());
	}

	@Test
	void disabledAccountCannotReusePreviouslyIssuedToken() throws Exception {
		String email = UUID.randomUUID() + "@example.test";
		String token = createUserAndLogin(email, UUID.randomUUID().toString(), RoleCode.STUDENT);
		UUID userId = userAccountRepository.findByEmailIgnoreCase(email).orElseThrow().getId();
		jdbcTemplate.update("UPDATE users SET status = 'DISABLED' WHERE id = ?", userId);

		HttpResponse<String> response = sendRequest("/api/v1/auth/me", token);

		assertEquals(401, response.statusCode());
		assertEquals("UNAUTHENTICATED", objectMapper.readTree(response.body()).path("code").asText());
	}

	private HttpResponse<String> sendRequest(String path) throws Exception {
		return sendRequest(path, null);
	}

	private HttpResponse<String> sendRequest(String path, String token) throws Exception {
		HttpRequest.Builder request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + path))
				.GET();
		if (token != null) {
			request.header("Authorization", "Bearer " + token);
		}
		return HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> postLogin(String email, String password) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + "/api/v1/auth/login"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
						new LoginBody(email, password))))
				.build();
		return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
	}

	private String createUserAndLogin(String email, String password, RoleCode roleCode) throws Exception {
		Role role = roleRepository.findById(roleCode).orElseThrow();
		UserAccount user = userAccountRepository.save(
				UserAccount.active(email, passwordEncoder.encode(password), java.util.Set.of(role), java.time.Instant.now()));
		HttpResponse<String> response = postLogin(user.getEmail(), password);
		assertEquals(200, response.statusCode());
		return objectMapper.readTree(response.body()).path("accessToken").asText();
	}

	private String bootstrapToken() throws Exception {
		HttpResponse<String> response = postLogin(BOOTSTRAP_EMAIL, BOOTSTRAP_PASSWORD);
		assertEquals(200, response.statusCode());
		return objectMapper.readTree(response.body()).path("accessToken").asText();
	}

	private static String createJwtSecret() {
		byte[] secret = new byte[32];
		new SecureRandom().nextBytes(secret);
		return Base64.getEncoder().encodeToString(secret);
	}

	record LoginBody(String email, String password) {
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class TestApiConfiguration {

		@Bean
		TestAuthorizationController testAuthorizationController() {
			return new TestAuthorizationController();
		}
	}

	@RestController
	static class TestAuthorizationController {

		@GetMapping("/api/v1/admin/test-access")
		String adminAccess() {
			return "allowed";
		}

		@GetMapping("/api/v1/students/test-admin-list")
		String studentAdminList() {
			return "allowed";
		}

		@GetMapping("/api/v1/students/me/{ownerUserId}/test-owned-resource")
		@PreAuthorize("@studentOwnershipAuthorization.isOwner(#p0, authentication)")
		String ownedResource(@PathVariable UUID ownerUserId) {
			return "owned";
		}
	}

}
