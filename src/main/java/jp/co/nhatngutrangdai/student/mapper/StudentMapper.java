package jp.co.nhatngutrangdai.student.mapper;

import jp.co.nhatngutrangdai.student.dto.StudentProfileResponse;
import jp.co.nhatngutrangdai.student.dto.StudentResponse;
import jp.co.nhatngutrangdai.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getUserAccount() == null ? null : student.getUserAccount().getId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getDateOfBirth(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress(),
                student.getNationality(),
                student.getStatus(),
                student.getCreatedAt(),
                student.getUpdatedAt());
    }

    public StudentProfileResponse toProfileResponse(Student student) {
        return new StudentProfileResponse(
                student.getId(),
                student.getStudentCode(),
                student.getFullName(),
                student.getDateOfBirth(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress(),
                student.getNationality(),
                student.getStatus());
    }
}
