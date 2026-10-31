package jp.co.nhatngutrangdai.auth.service;

import java.time.Instant;

public record IssuedAccessToken(String value, Instant expiresAt) {
}
