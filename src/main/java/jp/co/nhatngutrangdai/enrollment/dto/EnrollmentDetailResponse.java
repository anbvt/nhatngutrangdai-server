package jp.co.nhatngutrangdai.enrollment.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;

public record EnrollmentDetailResponse(
        UUID id,
        String applicantFullName,
        String applicantEmail,
        String applicantPhone,
        LocalDate applicantDateOfBirth,
        String applicantAddress,
        String applicantNationality,
        UUID courseId,
        EnrollmentStatus status,
        Instant submittedAt,
        List<EnrollmentStatusHistoryResponse> statusHistory) {
}
