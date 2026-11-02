package jp.co.nhatngutrangdai.course.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
}
