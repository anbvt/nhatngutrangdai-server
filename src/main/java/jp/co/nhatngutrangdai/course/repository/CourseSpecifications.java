package jp.co.nhatngutrangdai.course.repository;

import java.util.Locale;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.course.entity.Course;
import org.springframework.data.jpa.domain.Specification;

public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    public static Specification<Course> filter(String query, SchoolStatus status) {
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
            return predicates;
        };
    }
}
