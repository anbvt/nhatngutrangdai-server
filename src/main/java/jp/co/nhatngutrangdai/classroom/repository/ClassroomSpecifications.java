package jp.co.nhatngutrangdai.classroom.repository;

import java.util.Locale;
import java.util.UUID;

import jp.co.nhatngutrangdai.classroom.entity.Classroom;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ClassroomSpecifications {

    private ClassroomSpecifications() {
    }

    public static Specification<Classroom> filter(String query, SchoolStatus status, UUID courseId) {
        return (root, criteriaQuery, builder) -> {
            var predicates = builder.conjunction();
            if (query != null && !query.isBlank()) {
                String pattern = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
                predicates = builder.and(predicates, builder.or(
                        builder.like(builder.lower(root.get("code")), pattern),
                        builder.like(builder.lower(root.get("name")), pattern)));
            }
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
