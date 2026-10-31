package jp.co.nhatngutrangdai.config;

import java.time.Clock;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import jp.co.nhatngutrangdai.auth.service.JwtProperties;
import jp.co.nhatngutrangdai.auth.service.JwtTokenService;
import jp.co.nhatngutrangdai.auth.service.UserAccountAuthenticationConverter;
import jp.co.nhatngutrangdai.common.security.ApiSecurityExceptionHandler;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfiguration {

    @Bean
    SecretKey jwtSecretKey(JwtProperties properties) {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(properties.secret());
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_SECRET must be valid Base64 containing at least 32 bytes");
        }
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
        }
        if (properties.accessTokenTtl() == null || properties.accessTokenTtl().isZero()
                || properties.accessTokenTtl().isNegative()) {
            throw new IllegalStateException("JWT access-token TTL must be positive");
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey secretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey secretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(JwtTokenService.ISSUER));
        return decoder;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    Clock systemClock() {
        return Clock.systemUTC();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ApiSecurityExceptionHandler exceptionHandler,
            UserAccountAuthenticationConverter authenticationConverter,
            @Value("${springdoc.api-docs.path:/api-docs}") String apiDocsPath,
            @Value("${springdoc.swagger-ui.path:/swagger-ui.html}") String swaggerPath) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(exceptionHandler)
                        .accessDeniedHandler(exceptionHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", apiDocsPath + "/**", swaggerPath, "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers("/api/v1/auth/login")
                        .permitAll()
                        .requestMatchers("/api/v1/auth/me")
                        .authenticated()
                        .requestMatchers("/api/v1/admin/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/v1/students/me/**")
                        .hasRole("STUDENT")
                        .requestMatchers("/api/v1/students/**")
                        .hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/v1/**")
                        .denyAll()
                        .anyRequest()
                        .permitAll())
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter))
                        .authenticationEntryPoint(exceptionHandler)
                        .accessDeniedHandler(exceptionHandler));
        return http.build();
    }
}
