package jp.co.nhatngutrangdai.teacher.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeacherRepository extends JpaRepository<Teacher, UUID>, JpaSpecificationExecutor<Teacher> {
}
