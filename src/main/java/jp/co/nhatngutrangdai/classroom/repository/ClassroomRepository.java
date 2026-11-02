package jp.co.nhatngutrangdai.classroom.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.classroom.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID>, JpaSpecificationExecutor<Classroom> {
}
