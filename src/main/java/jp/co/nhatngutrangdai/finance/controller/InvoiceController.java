package jp.co.nhatngutrangdai.finance.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceCreateRequest;
import jp.co.nhatngutrangdai.finance.dto.InvoiceDetailResponse;
import jp.co.nhatngutrangdai.finance.dto.InvoiceSummaryResponse;
import jp.co.nhatngutrangdai.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final FinanceService financeService;

    public InvoiceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping
    public PageResponse<InvoiceSummaryResponse> list(
            @RequestParam(required = false) UUID studentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listInvoices(studentId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDetailResponse issue(
            @Valid @RequestBody InvoiceCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser actor) {
        return financeService.issueInvoice(request, actor);
    }

    @GetMapping("/{id}")
    public InvoiceDetailResponse get(@PathVariable UUID id) {
        return financeService.getInvoice(id);
    }
}
