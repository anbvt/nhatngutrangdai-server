package jp.co.nhatngutrangdai.student.dto;

import java.time.LocalDate;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;

public record StudentProfileResponse(
        UUID id,
        String studentCode,
        String fullName,
        LocalDate dateOfBirth,
        String email,
        String phone,
        String address,
        String nationality,
        SchoolStatus status) {
}
