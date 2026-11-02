package jp.co.nhatngutrangdai.student.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;

public record StudentResponse(
        UUID id,
        UUID userId,
        String studentCode,
        String fullName,
        LocalDate dateOfBirth,
        String email,
        String phone,
        String address,
        String nationality,
        SchoolStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
