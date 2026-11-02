package jp.co.nhatngutrangdai.teacher.dto;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TeacherCreateRequest(
        @NotBlank @Size(max = 160) String fullName,
        @Email @Size(max = 254) String email,
        @Size(max = 40) String phone,
        @NotNull SchoolStatus status) {
}
