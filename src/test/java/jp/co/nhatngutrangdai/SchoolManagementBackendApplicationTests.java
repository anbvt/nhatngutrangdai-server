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
import java.util.Map;

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
	void unknownRouteIsDeniedByDefaultSecurity() throws Exception {
		HttpResponse<String> response = sendRequest("/missing-route");

		assertEquals(401, response.statusCode());
		assertEquals("UNAUTHENTICATED", objectMapper.readTree(response.body()).path("code").asText());
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
		Long appliedCoreMigration = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '3'",
				Long.class);
		Long appliedEnrollmentMigration = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '4'",
				Long.class);

		assertEquals(1L, appliedFoundationMigration);
		assertEquals(1L, appliedAuthMigration);
		assertEquals(1L, appliedCoreMigration);
		assertEquals(1L, appliedEnrollmentMigration);
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
	void adminRoleCanAccessCoreReadEndpoints() throws Exception {
		String token = createUserAndLogin(
				UUID.randomUUID() + "@example.test",
				UUID.randomUUID().toString(),
				RoleCode.ADMIN);

		assertEquals(200, sendRequest("/api/v1/courses", token).statusCode());
		assertEquals(200, sendRequest("/api/v1/students", token).statusCode());
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
	void adminCanCreateAndFilterCoursesWithPagination() throws Exception {
		String token = bootstrapToken();
		HttpResponse<String> created = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of(
						"code", "PH3-" + UUID.randomUUID(),
						"name", "Japanese N5 course",
						"duration", "12 weeks",
						"tuitionAmount", 1200.00,
						"currency", "jpy",
						"status", "ACTIVE"));

		assertEquals(201, created.statusCode());
		var course = objectMapper.readTree(created.body());
		assertEquals("JPY", course.path("currency").asText());
		assertEquals("12 weeks", course.path("duration").asText());

		String code = course.path("code").asText();
		HttpResponse<String> list = sendRequest(
				"/api/v1/courses?q=" + code + "&status=ACTIVE&page=0&size=1&sort=code,asc",
				token);
		var page = objectMapper.readTree(list.body());

		assertEquals(200, list.statusCode());
		assertEquals(1, page.path("content").size());
		assertEquals(code, page.path("content").get(0).path("code").asText());
		assertEquals(1, page.path("size").asInt());
	}

	@Test
	void invalidSortFieldReturnsBadRequest() throws Exception {
		HttpResponse<String> response = sendRequest("/api/v1/courses?sort=passwordHash,asc", bootstrapToken());

		assertEquals(400, response.statusCode());
		assertEquals("INVALID_REQUEST", objectMapper.readTree(response.body()).path("code").asText());
	}

	@Test
	void coursePatchDistinguishesNullFromOmittedFields() throws Exception {
		String token = bootstrapToken();
		HttpResponse<String> created = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of("code", "P-" + UUID.randomUUID(), "name", "Before", "status", "ACTIVE"));
		assertEquals(201, created.statusCode(), created.body());
		String id = objectMapper.readTree(created.body()).path("id").asText();
		assertTrue(!id.isBlank(), created.body());

		HttpResponse<String> patched = sendJson(
				"/api/v1/courses/" + id,
				token,
				"PATCH",
				Map.of("description", ""));
		assertEquals(200, patched.statusCode());
		assertEquals("Before", objectMapper.readTree(patched.body()).path("name").asText());
		assertEquals("", objectMapper.readTree(patched.body()).path("description").asText());

		HttpResponse<String> cleared = sendJson(
				"/api/v1/courses/" + id,
				token,
				"PATCH",
				"{\"description\":null}");
		assertEquals(200, cleared.statusCode());
		assertTrue(objectMapper.readTree(cleared.body()).path("description").isNull());
	}

	@Test
	void studentCanOnlyReadAndUpdateTheirLinkedProfile() throws Exception {
		String adminToken = bootstrapToken();
		String firstEmail = UUID.randomUUID() + "@example.test";
		String secondEmail = UUID.randomUUID() + "@example.test";
		String firstPassword = UUID.randomUUID().toString();
		String secondPassword = UUID.randomUUID().toString();
		UserAccount firstUser = createUser(firstEmail, firstPassword, RoleCode.STUDENT);
		UserAccount secondUser = createUser(secondEmail, secondPassword, RoleCode.STUDENT);
		String firstStudentCode = "STU-" + UUID.randomUUID();
		String secondStudentCode = "STU-" + UUID.randomUUID();
		HttpResponse<String> firstCreated = createStudent(adminToken, firstStudentCode, firstUser.getId());
		HttpResponse<String> secondCreated = createStudent(adminToken, secondStudentCode, secondUser.getId());
		assertEquals(201, firstCreated.statusCode());
		assertEquals(201, secondCreated.statusCode());

		String firstToken = loginToken(firstEmail, firstPassword);
		String secondToken = loginToken(secondEmail, secondPassword);
		HttpResponse<String> firstProfile = sendRequest("/api/v1/students/me", firstToken);
		HttpResponse<String> secondProfile = sendRequest("/api/v1/students/me", secondToken);
		String firstStudentId = objectMapper.readTree(firstCreated.body()).path("id").asText();
		String secondStudentId = objectMapper.readTree(secondCreated.body()).path("id").asText();

		assertEquals(200, firstProfile.statusCode());
		assertEquals(200, secondProfile.statusCode());
		assertEquals(firstStudentId, objectMapper.readTree(firstProfile.body()).path("id").asText());
		assertEquals(secondStudentId, objectMapper.readTree(secondProfile.body()).path("id").asText());

		HttpResponse<String> updated = sendJson(
				"/api/v1/students/me",
				firstToken,
				"PATCH",
				"{\"phone\":\"12345\",\"address\":null}");
		var updatedProfile = objectMapper.readTree(updated.body());

		assertEquals(200, updated.statusCode());
		assertEquals("12345", updatedProfile.path("phone").asText());
		assertTrue(updatedProfile.path("address").isNull());
	}

	@Test
	void adminCanPatchAllCoreResourcesAndFilterTheirLists() throws Exception {
		String token = bootstrapToken();
		String courseCode = "F-" + UUID.randomUUID();
		HttpResponse<String> course = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of("code", courseCode, "name", "Before course", "status", "ACTIVE"));
		String courseId = objectMapper.readTree(course.body()).path("id").asText();
		String classroomCode = "R-" + UUID.randomUUID();
		HttpResponse<String> classroom = sendJson(
				"/api/v1/classrooms",
				token,
				"POST",
				Map.of(
						"courseId", courseId,
						"code", classroomCode,
						"name", "Before classroom",
						"capacity", 10,
						"status", "ACTIVE"));
		String teacherName = "Teacher-" + UUID.randomUUID();
		HttpResponse<String> teacher = sendJson(
				"/api/v1/teachers",
				token,
				"POST",
				Map.of("fullName", teacherName, "status", "ACTIVE"));
		String studentCode = "S-" + UUID.randomUUID();
		HttpResponse<String> student = createStudent(token, studentCode, null);

		assertEquals(200, sendJson(
				"/api/v1/courses/" + courseId,
				token,
				"PATCH",
				Map.of("name", "Updated course")).statusCode());
		assertEquals(200, sendJson(
				"/api/v1/classrooms/" + objectMapper.readTree(classroom.body()).path("id").asText(),
				token,
				"PATCH",
				Map.of("capacity", 12)).statusCode());
		assertEquals(200, sendJson(
				"/api/v1/teachers/" + objectMapper.readTree(teacher.body()).path("id").asText(),
				token,
				"PATCH",
				Map.of("status", "INACTIVE")).statusCode());
		assertEquals(200, sendJson(
				"/api/v1/students/" + objectMapper.readTree(student.body()).path("id").asText(),
				token,
				"PATCH",
				Map.of("fullName", "Updated student")).statusCode());

		assertEquals(1, objectMapper.readTree(sendRequest(
				"/api/v1/courses?q=" + courseCode + "&status=ACTIVE", token).body()).path("content").size());
		assertEquals(1, objectMapper.readTree(sendRequest(
				"/api/v1/classrooms?q=" + classroomCode + "&courseId=" + courseId, token).body()).path("content").size());
		assertEquals(1, objectMapper.readTree(sendRequest(
				"/api/v1/teachers?q=" + teacherName, token).body()).path("content").size());
		assertEquals(1, objectMapper.readTree(sendRequest(
				"/api/v1/students?q=" + studentCode + "&status=ACTIVE", token).body()).path("content").size());
	}

	@Test
	void studentCannotAccessAdminCoreDataEndpoints() throws Exception {
		String token = createUserAndLogin(
				UUID.randomUUID() + "@example.test",
				UUID.randomUUID().toString(),
				RoleCode.STUDENT);

		assertEquals(403, sendRequest("/api/v1/courses", token).statusCode());
		assertEquals(403, sendRequest("/api/v1/classrooms", token).statusCode());
		assertEquals(403, sendRequest("/api/v1/teachers", token).statusCode());
	}

	@Test
	void classroomRejectsInvalidDateRange() throws Exception {
		String token = bootstrapToken();
		HttpResponse<String> course = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of("code", "C-" + UUID.randomUUID(), "name", "Classroom course", "status", "ACTIVE"));
		String courseId = objectMapper.readTree(course.body()).path("id").asText();
		HttpResponse<String> classroom = sendJson(
				"/api/v1/classrooms",
				token,
				"POST",
				Map.of(
						"courseId", courseId,
						"code", "R-" + UUID.randomUUID(),
						"name", "Invalid date classroom",
						"capacity", 10,
						"startDate", "2026-03-02",
						"endDate", "2026-03-01",
						"status", "ACTIVE"));

		assertEquals(400, classroom.statusCode());
	}

	@Test
	void coreCrudEndpointsReturnDtosAndRespectAdminAuthorization() throws Exception {
		String token = bootstrapToken();
		HttpResponse<String> course = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of("code", "C-" + UUID.randomUUID(), "name", "Core course", "status", "ACTIVE"));
		assertEquals(201, course.statusCode(), course.body());
		String courseId = objectMapper.readTree(course.body()).path("id").asText();
		HttpResponse<String> classroom = sendJson(
				"/api/v1/classrooms",
				token,
				"POST",
				Map.of(
						"courseId", courseId,
						"code", "R-" + UUID.randomUUID(),
						"name", "Core classroom",
						"capacity", 12,
						"status", "ACTIVE"));
		HttpResponse<String> teacher = sendJson(
				"/api/v1/teachers",
				token,
				"POST",
				Map.of("fullName", "Teacher " + UUID.randomUUID(), "status", "ACTIVE"));
		HttpResponse<String> student = createStudent(token, "STU-" + UUID.randomUUID(), null);

		assertEquals(201, classroom.statusCode(), classroom.body());
		assertEquals(201, teacher.statusCode(), teacher.body());
		assertEquals(201, student.statusCode(), student.body());
		assertTrue(!objectMapper.readTree(student.body()).has("passwordHash"));
		assertEquals(200, sendRequest("/api/v1/classrooms/" + objectMapper.readTree(classroom.body()).path("id").asText(), token).statusCode());
		assertEquals(200, sendRequest("/api/v1/teachers/" + objectMapper.readTree(teacher.body()).path("id").asText(), token).statusCode());
		assertEquals(200, sendRequest("/api/v1/students/" + objectMapper.readTree(student.body()).path("id").asText(), token).statusCode());
	}

	@Test
	void duplicateStudentCodeConflictsAndStudentCannotListCoreData() throws Exception {
		String token = bootstrapToken();
		String studentCode = "DUP-" + UUID.randomUUID();
		HttpResponse<String> first = createStudent(token, studentCode, null);
		HttpResponse<String> duplicate = createStudent(token, studentCode, null);

		assertEquals(201, first.statusCode());
		assertEquals(409, duplicate.statusCode());
		assertEquals("CONFLICT", objectMapper.readTree(duplicate.body()).path("code").asText());
	}

	@Test
	void publicCanSubmitPendingApplicationAndOnlyAdminCanReviewIt() throws Exception {
		String adminToken = bootstrapToken();
		String courseId = createCourse(adminToken);
		Map<String, Object> application = Map.of(
				"fullName", "Applicant " + UUID.randomUUID(),
				"email", UUID.randomUUID() + "@example.test",
				"phone", "0900000000",
				"courseId", courseId);

		HttpResponse<String> submission = sendJson(
				"/api/v1/public/enrollment-applications", null, "POST", application);

		assertEquals(201, submission.statusCode(), submission.body());
		var receipt = objectMapper.readTree(submission.body());
		assertEquals("PENDING", receipt.path("status").asText());
		assertEquals(43, receipt.path("reference").asText().length());
		assertTrue(!receipt.has("applicantEmail"));

		HttpResponse<String> unauthorizedList = sendRequest("/api/v1/enrollments");
		assertEquals(401, unauthorizedList.statusCode());

		HttpResponse<String> adminList = sendRequest("/api/v1/enrollments?status=PENDING&courseId=" + courseId, adminToken);
		assertEquals(200, adminList.statusCode(), adminList.body());
		var list = objectMapper.readTree(adminList.body()).path("content");
		assertEquals(1, list.size());
		String enrollmentId = list.get(0).path("id").asText();
		HttpResponse<String> detail = sendRequest("/api/v1/enrollments/" + enrollmentId, adminToken);
		assertEquals(200, detail.statusCode(), detail.body());
		var detailBody = objectMapper.readTree(detail.body());
		assertEquals("PENDING", detailBody.path("status").asText());
		assertEquals(1, detailBody.path("statusHistory").size());
		assertTrue(detailBody.path("statusHistory").get(0).path("oldStatus").isNull());
		assertEquals("PENDING", detailBody.path("statusHistory").get(0).path("newStatus").asText());

		String studentToken = createUserAndLogin(
				UUID.randomUUID() + "@example.test",
				UUID.randomUUID().toString(),
				RoleCode.STUDENT);
		assertEquals(403, sendRequest("/api/v1/enrollments", studentToken).statusCode());
	}

	@Test
	void pendingApplicationsAreDeduplicatedByNormalizedEmailAndCourse() throws Exception {
		String courseId = createCourse(bootstrapToken());
		String email = UUID.randomUUID() + "@example.test";
		Map<String, Object> application = Map.of(
				"fullName", "Applicant",
				"email", email,
				"phone", "0900000000",
				"courseId", courseId);
		HttpResponse<String> first = sendJson(
				"/api/v1/public/enrollment-applications", null, "POST", application);
		Map<String, Object> duplicate = Map.of(
				"fullName", "Applicant Duplicate",
				"email", email.toUpperCase(java.util.Locale.ROOT),
				"phone", "0900000001",
				"courseId", courseId);
		HttpResponse<String> second = sendJson(
				"/api/v1/public/enrollment-applications", null, "POST", duplicate);

		assertEquals(201, first.statusCode(), first.body());
		assertEquals(409, second.statusCode(), second.body());
	}

	@Test
	void publicEnrollmentApplicationValidatesRequiredFieldsAndCourse() throws Exception {
		HttpResponse<String> invalid = sendJson(
				"/api/v1/public/enrollment-applications",
				null,
				"POST",
				Map.of("fullName", "Applicant"));
		assertEquals(400, invalid.statusCode());

		Map<String, Object> missingCourse = Map.of(
				"fullName", "Applicant",
				"email", UUID.randomUUID() + "@example.test",
				"phone", "0900000000",
				"courseId", UUID.randomUUID());
		HttpResponse<String> noCourse = sendJson(
				"/api/v1/public/enrollment-applications", null, "POST", missingCourse);
		assertEquals(404, noCourse.statusCode());
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
		return sendJson(path, token, "GET", (String) null);
	}

	private HttpResponse<String> sendJson(String path, String token, String method, Object body) throws Exception {
		String requestBody = body == null ? "" : body instanceof String text
				? text
				: objectMapper.writeValueAsString(body);
		HttpRequest.Builder request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + path))
				.header("Content-Type", "application/json");
		if (token != null) {
			request.header("Authorization", "Bearer " + token);
		}
		switch (method) {
			case "GET" -> request.GET();
			case "POST" -> request.POST(HttpRequest.BodyPublishers.ofString(requestBody));
			case "PATCH" -> request.method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody));
			default -> throw new IllegalArgumentException("Unsupported test HTTP method: " + method);
		}
		return HttpClient.newHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> createStudent(String token, String code, UUID userId) throws Exception {
		Map<String, Object> request = new java.util.HashMap<>();
		request.put("studentCode", code);
		request.put("fullName", "Student " + code);
		request.put("status", "ACTIVE");
		if (userId != null) {
			request.put("userId", userId);
		}
		return sendJson("/api/v1/students", token, "POST", request);
	}

	private String createCourse(String token) throws Exception {
		HttpResponse<String> course = sendJson(
				"/api/v1/courses",
				token,
				"POST",
				Map.of("code", "ENR-" + UUID.randomUUID(), "name", "Enrollment course", "status", "ACTIVE"));
		assertEquals(201, course.statusCode(), course.body());
		return objectMapper.readTree(course.body()).path("id").asText();
	}

	private String loginToken(String email, String password) throws Exception {
		HttpResponse<String> response = postLogin(email, password);
		assertEquals(200, response.statusCode());
		return objectMapper.readTree(response.body()).path("accessToken").asText();
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
		UserAccount user = createUser(email, password, roleCode);
		HttpResponse<String> response = postLogin(user.getEmail(), password);
		assertEquals(200, response.statusCode());
		return objectMapper.readTree(response.body()).path("accessToken").asText();
	}

	private UserAccount createUser(String email, String password, RoleCode roleCode) {
		Role role = roleRepository.findById(roleCode).orElseThrow();
		return userAccountRepository.save(
				UserAccount.active(email, passwordEncoder.encode(password), java.util.Set.of(role), java.time.Instant.now()));
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
