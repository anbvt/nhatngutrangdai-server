package jp.co.nhatngutrangdai.enrollment.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublicEnrollmentApplicationRequest(
        @NotBlank @Size(max = 160) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(max = 40) String phone,
        @NotNull UUID courseId,
        LocalDate dateOfBirth,
        @Size(max = 500) String address,
        @Size(max = 100) String nationality) {
}
