package jp.co.nhatngutrangdai.finance.controller;

import jp.co.nhatngutrangdai.auth.service.AuthenticatedUser;
import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.finance.dto.PaymentCreateRequest;
import jp.co.nhatngutrangdai.finance.dto.PaymentResponse;
import jp.co.nhatngutrangdai.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final FinanceService financeService;

    public PaymentController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping
    public PageResponse<PaymentResponse> list(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID invoiceId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listPayments(studentId, invoiceId, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse record(
            @Valid @RequestBody PaymentCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser actor) {
        return financeService.recordPayment(request, actor);
    }
}
