package jp.co.nhatngutrangdai.finance.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceDetailResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceSummaryResponse;
import jp.co.nhatngutrangdai.finance.dto.PaymentResponse;
import jp.co.nhatngutrangdai.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students/me")
@PreAuthorize("hasRole('STUDENT')")
@SecurityRequirement(name = "bearerAuth")
public class StudentFinanceController {

    private final FinanceService financeService;

    public StudentFinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/invoices")
    public PageResponse<InvoiceSummaryResponse> listInvoices(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listStudentInvoices(user.id(), pageable));
    }

    @GetMapping("/invoices/{id}")
    public InvoiceDetailResponse getInvoice(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return financeService.getStudentInvoice(user.id(), id);
    }

    @GetMapping("/payments")
    public PageResponse<PaymentResponse> listPayments(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listStudentPayments(user.id(), pageable));
    }
}
