package jp.co.nhatngutrangdai.student.repository;

import java.util.Optional;
import java.util.UUID;

import jp.co.nhatngutrangdai.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByUserAccount_Id(UUID userId);

    boolean existsByStudentCode(String studentCode);

    boolean existsByStudentCodeAndIdNot(String studentCode, UUID id);

    boolean existsByUserAccount_Id(UUID userId);

    boolean existsByUserAccount_IdAndIdNot(UUID userId, UUID id);
}
