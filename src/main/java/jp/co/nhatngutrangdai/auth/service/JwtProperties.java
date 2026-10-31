package jp.co.nhatngutrangdai.auth.service;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(String secret, Duration accessTokenTtl) {
}
