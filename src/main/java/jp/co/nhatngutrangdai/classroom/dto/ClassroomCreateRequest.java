package jp.co.nhatngutrangdai.classroom.dto;

import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ClassroomCreateRequest(
        @NotNull UUID courseId,
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 160) String name,
        @Positive int capacity,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull SchoolStatus status) {
}
