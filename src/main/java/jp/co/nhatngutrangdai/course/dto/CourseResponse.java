package jp.co.nhatngutrangdai.course.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;

public record CourseResponse(
        UUID id,
        String code,
        String name,
        String description,
        String level,
        String duration,
        BigDecimal tuitionAmount,
        String currency,
        SchoolStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
