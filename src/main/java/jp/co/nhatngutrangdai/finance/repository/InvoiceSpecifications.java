package jp.co.nhatngutrangdai.finance.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.Invoice;
import org.springframework.data.jpa.domain.Specification;

public final class InvoiceSpecifications {

    private InvoiceSpecifications() {
    }

    public static Specification<Invoice> filterByStudent(UUID studentId) {
        if (studentId == null) {
            return Specification.unrestricted();
        }
        return (root, query, builder) -> builder.equal(root.get("student").get("id"), studentId);
    }
}
