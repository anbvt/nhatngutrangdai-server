package jp.co.nhatngutrangdai.finance.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.TimestampedEntity;
import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import jp.co.nhatngutrangdai.student.entity.Student;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice extends TimestampedEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 40, updatable = false)
    private String invoiceNumber;

    @Column(name = "issue_date", nullable = false, updatable = false)
    private LocalDate issueDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "currency", nullable = false, length = 3, updatable = false)
    private String currency;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2, updatable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items = new ArrayList<>();

    protected Invoice() {
    }

    public static Invoice issue(
            Student student,
            Enrollment enrollment,
            String invoiceNumber,
            LocalDate issueDate,
            LocalDate dueDate,
            String currency,
            BigDecimal totalAmount) {
        Invoice invoice = new Invoice();
        invoice.student = student;
        invoice.enrollment = enrollment;
        invoice.invoiceNumber = invoiceNumber;
        invoice.issueDate = issueDate;
        invoice.dueDate = dueDate;
        invoice.currency = currency.toUpperCase(java.util.Locale.ROOT);
        invoice.totalAmount = totalAmount;
        return invoice;
    }

    public void addItem(int lineNumber, String description, int quantity, BigDecimal unitAmount) {
        items.add(InvoiceItem.create(
                this,
                lineNumber,
                description,
                quantity,
                unitAmount,
                unitAmount.multiply(BigDecimal.valueOf(quantity))));
    }

    public UUID getId() { return id; }
    public Student getStudent() { return student; }
    public Enrollment getEnrollment() { return enrollment; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public String getCurrency() { return currency; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<InvoiceItem> getItems() { return List.copyOf(items); }
}
