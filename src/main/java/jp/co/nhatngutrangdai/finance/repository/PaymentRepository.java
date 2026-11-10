package jp.co.nhatngutrangdai.finance.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    @Query("select coalesce(sum(payment.amount), 0) from Payment payment where payment.invoice.id = :invoiceId")
    BigDecimal sumAmountByInvoiceId(@Param("invoiceId") UUID invoiceId);

    @Query("""
            select payment.invoice.id as invoiceId, sum(payment.amount) as paidAmount
            from Payment payment
            where payment.invoice.id in :invoiceIds
            group by payment.invoice.id
            """)
    List<InvoicePaymentTotal> sumAmountsByInvoiceIds(@Param("invoiceIds") Collection<UUID> invoiceIds);
}
