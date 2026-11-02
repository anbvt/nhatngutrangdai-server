package jp.co.nhatngutrangdai.course.service;

import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.course.dto.CourseCreateRequest;
import jp.co.nhatngutrangdai.course.dto.CoursePatchRequest;
import jp.co.nhatngutrangdai.course.dto.CourseResponse;
import jp.co.nhatngutrangdai.course.entity.Course;
import jp.co.nhatngutrangdai.course.mapper.CourseMapper;
import jp.co.nhatngutrangdai.course.repository.CourseRepository;
import jp.co.nhatngutrangdai.course.repository.CourseSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "code", "name", "status", "createdAt", "updatedAt");

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(
            CourseRepository courseRepository,
            CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @Transactional(readOnly = true)
    public Page<CourseResponse> list(String query, SchoolStatus status, Pageable pageable) {
        validateQuery(query);
        Pageable safePageable = PageableSupport.validated(pageable, SORT_FIELDS, "code");
        return courseRepository.findAll(CourseSpecifications.filter(query, status), safePageable)
                .map(courseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CourseResponse get(UUID id) {
        return courseMapper.toResponse(findCourse(id));
    }

    @Transactional
    public CourseResponse create(CourseCreateRequest request) {
        Course course = Course.create(
                request.code(),
                request.name(),
                request.description(),
                request.level(),
                request.duration(),
                request.tuitionAmount(),
                request.currency(),
                request.status());
        return courseMapper.toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(UUID id, CoursePatchRequest request) {
        Course course = findCourse(id);
        if (request.codePresent()) {
            course.setCode(requireNonBlank(request.code(), "code"));
        }
        if (request.namePresent()) {
            course.setName(requireNonBlank(request.name(), "name"));
        }
        if (request.descriptionPresent()) {
            course.setDescription(request.description());
        }
        if (request.levelPresent()) {
            course.setLevel(request.level());
        }
        if (request.durationPresent()) {
            course.setDuration(request.duration());
        }
        if (request.tuitionAmountPresent()) {
            course.setTuitionAmount(request.tuitionAmount());
        }
        if (request.currencyPresent()) {
            course.setCurrency(request.currency());
        }
        if (request.statusPresent()) {
            course.setStatus(requirePresent(request.status(), "status"));
        }
        courseRepository.flush();
        return courseMapper.toResponse(course);
    }

    private Course findCourse(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Course"));
    }

    private String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw ApiExceptionFactory.badRequest(field + " cannot be null or blank");
        }
        return value;
    }

    private <T> T requirePresent(T value, String field) {
        if (value == null) {
            throw ApiExceptionFactory.badRequest(field + " cannot be null");
        }
        return value;
    }

    private void validateQuery(String query) {
        if (query != null && query.length() > 120) {
            throw ApiExceptionFactory.badRequest("Search query must be at most 120 characters");
        }
    }
}
