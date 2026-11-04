package jp.co.nhatngutrangdai.enrollment.dto;

import java.time.Instant;

import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;

public record PublicEnrollmentApplicationResponse(
        String reference,
        EnrollmentStatus status,
        Instant submittedAt) {
}
