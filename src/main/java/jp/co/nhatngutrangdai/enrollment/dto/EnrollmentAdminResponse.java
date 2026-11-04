package jp.co.nhatngutrangdai.enrollment.dto;

import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;

public record EnrollmentAdminResponse(
        UUID id,
        String applicantFullName,
        String applicantEmail,
        String applicantPhone,
        UUID courseId,
        EnrollmentStatus status,
        Instant submittedAt) {
}
