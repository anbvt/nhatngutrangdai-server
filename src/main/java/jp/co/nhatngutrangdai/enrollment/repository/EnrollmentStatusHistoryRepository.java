package jp.co.nhatngutrangdai.enrollment.repository;

import java.util.List;
import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentStatusHistoryRepository extends JpaRepository<EnrollmentStatusHistory, UUID> {

    List<EnrollmentStatusHistory> findAllByEnrollment_IdOrderByOccurredAtAsc(UUID enrollmentId);
}
