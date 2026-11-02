package jp.co.nhatngutrangdai.teacher.mapper;

import jp.co.nhatngutrangdai.teacher.dto.TeacherResponse;
import jp.co.nhatngutrangdai.teacher.entity.Teacher;
import org.springframework.stereotype.Component;

@Component
public class TeacherMapper {

    public TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.getPhone(),
                teacher.getStatus(),
                teacher.getCreatedAt(),
                teacher.getUpdatedAt());
    }
}
