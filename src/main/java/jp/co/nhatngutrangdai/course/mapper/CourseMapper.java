package jp.co.nhatngutrangdai.course.mapper;

import jp.co.nhatngutrangdai.course.dto.CourseResponse;
import jp.co.nhatngutrangdai.course.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getCode(),
                course.getName(),
                course.getDescription(),
                course.getLevel(),
                course.getDuration(),
                course.getTuitionAmount(),
                course.getCurrency(),
                course.getStatus(),
                course.getCreatedAt(),
                course.getUpdatedAt());
    }
}
