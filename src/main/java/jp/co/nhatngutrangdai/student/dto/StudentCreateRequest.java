package jp.co.nhatngutrangdai.student.dto;

import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentCreateRequest(
        @NotBlank @Size(max = 40) String studentCode,
        @NotBlank @Size(max = 160) String fullName,
        LocalDate dateOfBirth,
        @Email @Size(max = 254) String email,
        @Size(max = 40) String phone,
        @Size(max = 500) String address,
        @Size(max = 100) String nationality,
        @NotNull SchoolStatus status,
        UUID userId) {
}
