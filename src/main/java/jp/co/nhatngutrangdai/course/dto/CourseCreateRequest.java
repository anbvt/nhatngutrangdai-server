package jp.co.nhatngutrangdai.course.dto;

import java.math.BigDecimal;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 160) String name,
        @Size(max = 2000) String description,
        @Size(max = 80) String level,
        @Size(max = 120) String duration,
        @DecimalMin(value = "0.00") @Digits(integer = 10, fraction = 2) BigDecimal tuitionAmount,
        @Pattern(regexp = "[A-Za-z]{3}") String currency,
        @NotNull SchoolStatus status) {
}
