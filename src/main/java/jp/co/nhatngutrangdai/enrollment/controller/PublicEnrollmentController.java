package jp.co.nhatngutrangdai.enrollment.controller;

import jp.co.nhatngutrangdai.enrollment.dto.PublicEnrollmentApplicationRequest;
import jp.co.nhatngutrangdai.enrollment.dto.PublicEnrollmentApplicationResponse;
import jp.co.nhatngutrangdai.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/enrollment-applications")
public class PublicEnrollmentController {

    private final EnrollmentService enrollmentService;

    public PublicEnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicEnrollmentApplicationResponse submit(
            @Valid @RequestBody PublicEnrollmentApplicationRequest request) {
        return enrollmentService.submit(request);
    }
}
