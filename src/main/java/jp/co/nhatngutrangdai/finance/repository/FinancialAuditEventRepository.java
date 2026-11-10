package jp.co.nhatngutrangdai.finance.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.FinancialAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialAuditEventRepository extends JpaRepository<FinancialAuditEvent, UUID> {
}
