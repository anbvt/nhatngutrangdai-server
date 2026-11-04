package jp.co.nhatngutrangdai.enrollment.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {

    boolean existsByPendingDuplicateKey(String pendingDuplicateKey);
}
