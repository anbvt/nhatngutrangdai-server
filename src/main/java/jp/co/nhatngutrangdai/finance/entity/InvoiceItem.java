package jp.co.nhatngutrangdai.finance.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitAmount;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    protected InvoiceItem() {
    }

    static InvoiceItem create(
            Invoice invoice,
            int lineNumber,
            String description,
            int quantity,
            BigDecimal unitAmount,
            BigDecimal lineTotal) {
        InvoiceItem item = new InvoiceItem();
        item.invoice = invoice;
        item.lineNumber = lineNumber;
        item.description = description.trim();
        item.quantity = quantity;
        item.unitAmount = unitAmount;
        item.lineTotal = lineTotal;
        return item;
    }

    public UUID getId() { return id; }
    public int getLineNumber() { return lineNumber; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitAmount() { return unitAmount; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
