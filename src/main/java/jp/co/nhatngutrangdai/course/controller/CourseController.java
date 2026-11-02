package jp.co.nhatngutrangdai.course.controller;

import java.util.UUID;

import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.course.dto.CourseCreateRequest;
import jp.co.nhatngutrangdai.course.dto.CoursePatchRequest;
import jp.co.nhatngutrangdai.course.dto.CourseResponse;
import jp.co.nhatngutrangdai.course.service.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public PageResponse<CourseResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SchoolStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(courseService.list(q, status, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CourseCreateRequest request) {
        return courseService.create(request);
    }

    @GetMapping("/{id}")
    public CourseResponse get(@PathVariable UUID id) {
        return courseService.get(id);
    }

    @PatchMapping("/{id}")
    public CourseResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CoursePatchRequest request) {
        return courseService.update(id, request);
    }
}
