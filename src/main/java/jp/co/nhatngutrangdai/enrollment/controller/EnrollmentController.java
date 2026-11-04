package jp.co.nhatngutrangdai.enrollment.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentAdminResponse;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentDetailResponse;
import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;
import jp.co.nhatngutrangdai.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public PageResponse<EnrollmentAdminResponse> list(
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(required = false) UUID courseId,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(enrollmentService.list(status, courseId, pageable));
    }

    @GetMapping("/{id}")
    public EnrollmentDetailResponse get(@PathVariable UUID id) {
        return enrollmentService.get(id);
    }
}
