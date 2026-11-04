package jp.co.nhatngutrangdai.enrollment.mapper;

import java.util.List;

import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentAdminResponse;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentDetailResponse;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentStatusHistoryResponse;
import jp.co.nhatngutrangdai.enrollment.dto.PublicEnrollmentApplicationResponse;
import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatusHistory;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {

    public PublicEnrollmentApplicationResponse toPublicResponse(Enrollment enrollment, String reference) {
        return new PublicEnrollmentApplicationResponse(
                reference,
                enrollment.getStatus(),
                enrollment.getSubmittedAt());
    }

    public EnrollmentAdminResponse toAdminResponse(Enrollment enrollment) {
        return new EnrollmentAdminResponse(
                enrollment.getId(),
                enrollment.getApplicantFullName(),
                enrollment.getApplicantEmail(),
                enrollment.getApplicantPhone(),
                enrollment.getCourse().getId(),
                enrollment.getStatus(),
                enrollment.getSubmittedAt());
    }

    public EnrollmentDetailResponse toDetailResponse(
            Enrollment enrollment,
            List<EnrollmentStatusHistory> history) {
        return new EnrollmentDetailResponse(
                enrollment.getId(),
                enrollment.getApplicantFullName(),
                enrollment.getApplicantEmail(),
                enrollment.getApplicantPhone(),
                enrollment.getApplicantDateOfBirth(),
                enrollment.getApplicantAddress(),
                enrollment.getApplicantNationality(),
                enrollment.getCourse().getId(),
                enrollment.getStatus(),
                enrollment.getSubmittedAt(),
                history.stream().map(this::toHistoryResponse).toList());
    }

    private EnrollmentStatusHistoryResponse toHistoryResponse(EnrollmentStatusHistory history) {
        return new EnrollmentStatusHistoryResponse(
                history.getOldStatus(),
                history.getNewStatus(),
                history.getActor() == null ? null : history.getActor().getId(),
                history.getOccurredAt(),
                history.getNote());
    }
}
