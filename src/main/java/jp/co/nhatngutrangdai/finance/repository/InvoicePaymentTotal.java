package jp.co.nhatngutrangdai.finance.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface InvoicePaymentTotal {

    UUID getInvoiceId();

    BigDecimal getPaidAmount();
}
