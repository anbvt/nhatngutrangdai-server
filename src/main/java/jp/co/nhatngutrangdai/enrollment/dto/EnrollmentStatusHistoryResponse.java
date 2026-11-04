package jp.co.nhatngutrangdai.enrollment.dto;

import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;

public record EnrollmentStatusHistoryResponse(
        EnrollmentStatus oldStatus,
        EnrollmentStatus newStatus,
        UUID actorUserId,
        Instant occurredAt,
        String note) {
}
