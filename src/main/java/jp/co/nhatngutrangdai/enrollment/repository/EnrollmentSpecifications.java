package jp.co.nhatngutrangdai.enrollment.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;
import org.springframework.data.jpa.domain.Specification;

public final class EnrollmentSpecifications {

    private EnrollmentSpecifications() {
    }

    public static Specification<Enrollment> filter(EnrollmentStatus status, UUID courseId) {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();
            if (status != null) {
                predicates = builder.and(predicates, builder.equal(root.get("status"), status));
            }
            if (courseId != null) {
                predicates = builder.and(predicates, builder.equal(root.get("course").get("id"), courseId));
            }
            return predicates;
        };
    }
}
