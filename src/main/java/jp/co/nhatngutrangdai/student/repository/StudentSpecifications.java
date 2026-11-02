package jp.co.nhatngutrangdai.student.repository;

import java.util.Locale;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.student.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public final class StudentSpecifications {

    private StudentSpecifications() {
    }

    public static Specification<Student> filter(String query, SchoolStatus status) {
        return (root, criteriaQuery, builder) -> {
            var predicates = builder.conjunction();
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates = builder.and(predicates, builder.or(
                        builder.like(builder.lower(root.get("studentCode")), pattern),
                        builder.like(builder.lower(root.get("fullName")), pattern),
                        builder.like(builder.lower(root.get("email")), pattern)));
            }
            if (status != null) {
                predicates = builder.and(predicates, builder.equal(root.get("status"), status));
            }
            return predicates;
        };
    }
}
