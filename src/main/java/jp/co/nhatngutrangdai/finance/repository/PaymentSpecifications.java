package jp.co.nhatngutrangdai.finance.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentSpecifications {

    private PaymentSpecifications() {
    }

    public static Specification<Payment> filter(UUID studentId, UUID invoiceId) {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();
            if (studentId != null) {
                predicates = builder.and(
                        predicates,
                        builder.equal(root.get("invoice").get("student").get("id"), studentId));
            }
            if (invoiceId != null) {
                predicates = builder.and(predicates, builder.equal(root.get("invoice").get("id"), invoiceId));
            }
            return predicates;
        };
    }
}
