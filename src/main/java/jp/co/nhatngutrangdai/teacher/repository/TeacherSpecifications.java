package jp.co.nhatngutrangdai.teacher.repository;

import java.util.Locale;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.teacher.entity.Teacher;
import org.springframework.data.jpa.domain.Specification;

public final class TeacherSpecifications {

    private TeacherSpecifications() {
    }

    public static Specification<Teacher> filter(String query, SchoolStatus status) {
        return (root, criteriaQuery, builder) -> {
            var predicates = builder.conjunction();
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates = builder.and(predicates, builder.or(
                        builder.like(builder.lower(root.get("fullName")), pattern),
                        builder.like(builder.lower(root.get("email")), pattern),
                        builder.like(builder.lower(root.get("phone")), pattern)));
            }
            if (status != null) {
                predicates = builder.and(predicates, builder.equal(root.get("status"), status));
            }
            return predicates;
        };
    }
}
