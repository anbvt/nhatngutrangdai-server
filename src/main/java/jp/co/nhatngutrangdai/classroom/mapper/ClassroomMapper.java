package jp.co.nhatngutrangdai.classroom.mapper;

import jp.co.nhatngutrangdai.classroom.dto.ClassroomResponse;
import jp.co.nhatngutrangdai.classroom.entity.Classroom;
import org.springframework.stereotype.Component;

@Component
public class ClassroomMapper {

    public ClassroomResponse toResponse(Classroom classroom) {
        return new ClassroomResponse(
                classroom.getId(),
                classroom.getCourse().getId(),
                classroom.getCourse().getCode(),
                classroom.getCourse().getName(),
                classroom.getCode(),
                classroom.getName(),
                classroom.getCapacity(),
                classroom.getStartDate(),
                classroom.getEndDate(),
                classroom.getStatus(),
                classroom.getCreatedAt(),
                classroom.getUpdatedAt());
    }
}
