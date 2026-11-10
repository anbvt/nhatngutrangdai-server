package jp.co.nhatngutrangdai.finance.controller;

import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.finance.dto.FinancialTransactionResponse;
import jp.co.nhatngutrangdai.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class FinancialTransactionController {

    private final FinanceService financeService;

    public FinancialTransactionController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping
    public PageResponse<FinancialTransactionResponse> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(financeService.listTransactions(pageable));
    }
}
