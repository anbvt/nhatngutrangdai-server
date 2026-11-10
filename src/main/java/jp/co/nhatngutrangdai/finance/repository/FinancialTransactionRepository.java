package jp.co.nhatngutrangdai.finance.repository;

import java.util.UUID;

import jp.co.nhatngutrangdai.finance.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FinancialTransactionRepository extends
        JpaRepository<FinancialTransaction, UUID>,
        JpaSpecificationExecutor<FinancialTransaction> {
}
