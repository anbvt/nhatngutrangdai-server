package jp.co.nhatngutrangdai.teacher.dto;

import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;

public record TeacherResponse(
        UUID id,
        String fullName,
        String email,
        String phone,
        SchoolStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
