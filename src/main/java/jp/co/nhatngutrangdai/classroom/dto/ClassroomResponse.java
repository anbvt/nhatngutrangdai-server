package jp.co.nhatngutrangdai.classroom.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;

public record ClassroomResponse(
        UUID id,
        UUID courseId,
        String courseCode,
        String courseName,
        String code,
        String name,
        int capacity,
        LocalDate startDate,
        LocalDate endDate,
        SchoolStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
