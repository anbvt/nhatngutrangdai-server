package jp.co.nhatngutrangdai.finance.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.auth.repository.UserAccountRepository;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import jp.co.nhatngutrangdai.enrollment.repository.EnrollmentRepository;
import jp.co.nhatngutrangdai.finance.dto.FinancialTransactionResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceCreateRequest;
import jp.co.nhatngutrangdai.finance.dto.InvoiceDetailResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceSummaryResponse;
import jp.co.nhatngutrangdai.finance.dto.PaymentCreateRequest;
import jp.co.nhatngutrangdai.finance.dto.PaymentResponse;
import jp.co.nhatngutrangdai.finance.entity.FinancialAuditEvent;
import jp.co.nhatngutrangdai.finance.entity.FinancialTransaction;
import jp.co.nhatngutrangdai.finance.entity.Invoice;
import jp.co.nhatngutrangdai.finance.entity.InvoiceItem;
import jp.co.nhatngutrangdai.finance.entity.Payment;
import jp.co.nhatngutrangdai.finance.mapper.FinanceMapper;
import jp.co.nhatngutrangdai.finance.repository.FinancialAuditEventRepository;
import jp.co.nhatngutrangdai.finance.repository.FinancialTransactionRepository;
import jp.co.nhatngutrangdai.finance.repository.InvoicePaymentTotal;
import jp.co.nhatngutrangdai.finance.repository.InvoiceRepository;
import jp.co.nhatngutrangdai.finance.repository.InvoiceSpecifications;
import jp.co.nhatngutrangdai.finance.repository.PaymentRepository;
import jp.co.nhatngutrangdai.finance.repository.PaymentSpecifications;
import jp.co.nhatngutrangdai.student.entity.Student;
import jp.co.nhatngutrangdai.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceService {

    private static final BigDecimal MAX_MONEY_AMOUNT = new BigDecimal("9999999999.99");
    private static final Set<String> INVOICE_SORT_FIELDS = Set.of(
            "invoiceNumber", "issueDate", "dueDate", "totalAmount", "createdAt");
    private static final Set<String> PAYMENT_SORT_FIELDS = Set.of("paidAt", "amount", "createdAt");
    private static final Set<String> TRANSACTION_SORT_FIELDS = Set.of("occurredAt", "amount");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final FinancialAuditEventRepository auditEventRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final FinanceMapper financeMapper;
    private final JdbcTemplate jdbcTemplate;
    private final Clock clock;

    public FinanceService(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            FinancialTransactionRepository transactionRepository,
            FinancialAuditEventRepository auditEventRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            UserAccountRepository userAccountRepository,
            FinanceMapper financeMapper,
            JdbcTemplate jdbcTemplate,
            Clock clock) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.auditEventRepository = auditEventRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.financeMapper = financeMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.clock = clock;
    }

    @Transactional
    public InvoiceDetailResponse issueInvoice(InvoiceCreateRequest request, AuthenticatedUser actor) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> ApiExceptionFactory.notFound("Student"));
        Enrollment enrollment = request.enrollmentId() == null
                ? null
                : findEnrollmentForStudent(request.enrollmentId(), student.getId());
        LocalDate issueDate = LocalDate.now(clock);
        if (request.dueDate() != null && request.dueDate().isBefore(issueDate)) {
            throw ApiExceptionFactory.badRequest("dueDate must not be before issueDate");
        }
        String currency = request.currency().trim().toUpperCase(java.util.Locale.ROOT);
        BigDecimal totalAmount = request.items().stream()
                .map(item -> item.unitAmount().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalAmount.signum() <= 0) {
            throw ApiExceptionFactory.badRequest("Invoice total amount must be greater than zero");
        }
        if (totalAmount.compareTo(MAX_MONEY_AMOUNT) > 0) {
            throw ApiExceptionFactory.badRequest("Invoice total amount exceeds the supported precision");
        }

        UserAccount actorAccount = findActor(actor.id());
        long sequence = nextInvoiceNumberSequence();
        String invoiceNumber = String.format(
                java.util.Locale.ROOT,
                "INV-%d-%06d",
                issueDate.getYear(),
                sequence);
        Invoice invoice = Invoice.issue(
                student,
                enrollment,
                invoiceNumber,
                issueDate,
                request.dueDate(),
                currency,
                totalAmount);
        for (int index = 0; index < request.items().size(); index++) {
            var item = request.items().get(index);
            invoice.addItem(index + 1, item.description(), item.quantity(), item.unitAmount());
        }

        Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);
        auditEventRepository.save(FinancialAuditEvent.record(
                actorAccount,
                "INVOICE_ISSUED",
                "INVOICE",
                savedInvoice.getId(),
                clock.instant()));
        return financeMapper.toDetail(savedInvoice, BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> listInvoices(UUID studentId, Pageable pageable) {
        Pageable safePageable = PageableSupport.validated(pageable, INVOICE_SORT_FIELDS, "issueDate");
        Page<Invoice> invoices = invoiceRepository.findAll(
                InvoiceSpecifications.filterByStudent(studentId),
                safePageable);
        Map<UUID, BigDecimal> paidAmounts = paidAmounts(invoices.getContent());
        return invoices.map(invoice -> financeMapper.toSummary(
                invoice,
                paidAmounts.getOrDefault(invoice.getId(), BigDecimal.ZERO)));
    }

    @Transactional(readOnly = true)
    public InvoiceDetailResponse getInvoice(UUID invoiceId) {
        Invoice invoice = findInvoice(invoiceId);
        return financeMapper.toDetail(invoice, paymentRepository.sumAmountByInvoiceId(invoiceId));
    }

    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> listStudentInvoices(UUID userId, Pageable pageable) {
        Student student = findStudentForUser(userId);
        Pageable safePageable = PageableSupport.validated(pageable, INVOICE_SORT_FIELDS, "issueDate");
        Page<Invoice> invoices = invoiceRepository.findAll(
                InvoiceSpecifications.filterByStudent(student.getId()),
                safePageable);
        Map<UUID, BigDecimal> paidAmounts = paidAmounts(invoices.getContent());
        return invoices.map(invoice -> financeMapper.toSummary(
                invoice,
                paidAmounts.getOrDefault(invoice.getId(), BigDecimal.ZERO)));
    }

    @Transactional(readOnly = true)
    public InvoiceDetailResponse getStudentInvoice(UUID userId, UUID invoiceId) {
        Student student = findStudentForUser(userId);
        Invoice invoice = invoiceRepository.findByIdAndStudent_Id(invoiceId, student.getId())
                .orElseThrow(() -> ApiExceptionFactory.notFound("Invoice"));
        return financeMapper.toDetail(invoice, paymentRepository.sumAmountByInvoiceId(invoiceId));
    }

    @Transactional
    public PaymentResponse recordPayment(PaymentCreateRequest request, AuthenticatedUser actor) {
        Invoice invoice = invoiceRepository.findByIdForUpdate(request.invoiceId())
                .orElseThrow(() -> ApiExceptionFactory.notFound("Invoice"));
        BigDecimal paidAmount = paymentRepository.sumAmountByInvoiceId(invoice.getId());
        BigDecimal balance = invoice.getTotalAmount().subtract(paidAmount);
        if (request.amount().compareTo(balance) > 0) {
            throw ApiExceptionFactory.conflict("Payment amount exceeds the invoice balance");
        }

        Instant paidAt = clock.instant();
        UserAccount actorAccount = findActor(actor.id());
        Payment payment = paymentRepository.saveAndFlush(Payment.record(
                invoice,
                request.amount(),
                request.method(),
                paidAt,
                request.externalReference(),
                request.note(),
                actorAccount));
        transactionRepository.saveAndFlush(FinancialTransaction.forPayment(payment, actorAccount, paidAt));
        auditEventRepository.save(FinancialAuditEvent.record(
                actorAccount,
                "PAYMENT_RECORDED",
                "PAYMENT",
                payment.getId(),
                paidAt));
        return financeMapper.toPayment(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> listPayments(UUID studentId, UUID invoiceId, Pageable pageable) {
        Pageable safePageable = PageableSupport.validated(pageable, PAYMENT_SORT_FIELDS, "paidAt");
        return paymentRepository.findAll(PaymentSpecifications.filter(studentId, invoiceId), safePageable)
                .map(financeMapper::toPayment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> listStudentPayments(UUID userId, Pageable pageable) {
        Student student = findStudentForUser(userId);
        return listPayments(student.getId(), null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<FinancialTransactionResponse> listTransactions(Pageable pageable) {
        Pageable safePageable = PageableSupport.validated(pageable, TRANSACTION_SORT_FIELDS, "occurredAt");
        return transactionRepository.findAll(safePageable).map(financeMapper::toTransaction);
    }

    private Map<UUID, BigDecimal> paidAmounts(Collection<Invoice> invoices) {
        if (invoices.isEmpty()) {
            return Map.of();
        }
        List<UUID> invoiceIds = invoices.stream().map(Invoice::getId).toList();
        Map<UUID, BigDecimal> result = new HashMap<>();
        for (InvoicePaymentTotal total : paymentRepository.sumAmountsByInvoiceIds(invoiceIds)) {
            result.put(total.getInvoiceId(), total.getPaidAmount());
        }
        return result;
    }

    private long nextInvoiceNumberSequence() {
        Long sequence = jdbcTemplate.queryForObject("SELECT nextval('invoice_number_seq')", Long.class);
        if (sequence == null) {
            throw new IllegalStateException("Invoice number sequence returned no value");
        }
        return sequence;
    }

    private Invoice findInvoice(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Invoice"));
    }

    private Enrollment findEnrollmentForStudent(UUID enrollmentId, UUID studentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Enrollment"));
        if (enrollment.getStudent() == null || !enrollment.getStudent().getId().equals(studentId)) {
            throw ApiExceptionFactory.badRequest("Enrollment must belong to the invoice student");
        }
        return enrollment;
    }

    private Student findStudentForUser(UUID userId) {
        return studentRepository.findByUserAccount_Id(userId)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Student profile"));
    }

    private UserAccount findActor(UUID userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> ApiExceptionFactory.notFound("User account"));
    }

}
