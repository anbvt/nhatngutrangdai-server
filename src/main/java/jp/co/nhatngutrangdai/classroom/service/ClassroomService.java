package jp.co.nhatngutrangdai.classroom.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.classroom.dto.ClassroomCreateRequest;
import jp.co.nhatngutrangdai.classroom.dto.ClassroomPatchRequest;
import jp.co.nhatngutrangdai.classroom.dto.ClassroomResponse;
import jp.co.nhatngutrangdai.classroom.entity.Classroom;
import jp.co.nhatngutrangdai.classroom.mapper.ClassroomMapper;
import jp.co.nhatngutrangdai.classroom.repository.ClassroomRepository;
import jp.co.nhatngutrangdai.classroom.repository.ClassroomSpecifications;
import jp.co.nhatngutrangdai.common.entity.SchoolStatus;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.course.entity.Course;
import jp.co.nhatngutrangdai.course.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassroomService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "code", "name", "capacity", "startDate", "endDate", "status", "createdAt", "updatedAt");

    private final ClassroomRepository classroomRepository;
    private final CourseRepository courseRepository;
    private final ClassroomMapper classroomMapper;

    public ClassroomService(
            ClassroomRepository classroomRepository,
            CourseRepository courseRepository,
            ClassroomMapper classroomMapper) {
        this.classroomRepository = classroomRepository;
        this.courseRepository = courseRepository;
        this.classroomMapper = classroomMapper;
    }

    @Transactional(readOnly = true)
    public Page<ClassroomResponse> list(
            String query,
            SchoolStatus status,
            UUID courseId,
            Pageable pageable) {
        validateQuery(query);
        Pageable safePageable = PageableSupport.validated(pageable, SORT_FIELDS, "code");
        return classroomRepository.findAll(
                        ClassroomSpecifications.filter(query, status, courseId),
                        safePageable)
                .map(classroomMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClassroomResponse get(UUID id) {
        return classroomMapper.toResponse(findClassroom(id));
    }

    @Transactional
    public ClassroomResponse create(ClassroomCreateRequest request) {
        validateDates(request.startDate(), request.endDate());
        Course course = findCourse(request.courseId());
        Classroom classroom = Classroom.create(
                course,
                request.code(),
                request.name(),
                request.capacity(),
                request.startDate(),
                request.endDate(),
                request.status());
        return classroomMapper.toResponse(classroomRepository.save(classroom));
    }

    @Transactional
    public ClassroomResponse update(UUID id, ClassroomPatchRequest request) {
        Classroom classroom = findClassroom(id);
        if (request.courseIdPresent()) {
            classroom.setCourse(findCourse(requirePresent(request.courseId(), "courseId")));
        }
        if (request.codePresent()) {
            classroom.setCode(requireNonBlank(request.code(), "code"));
        }
        if (request.namePresent()) {
            classroom.setName(requireNonBlank(request.name(), "name"));
        }
        if (request.capacityPresent()) {
            classroom.setCapacity(requirePresent(request.capacity(), "capacity"));
        }
        LocalDate startDate = request.startDatePresent() ? request.startDate() : classroom.getStartDate();
        LocalDate endDate = request.endDatePresent() ? request.endDate() : classroom.getEndDate();
        validateDates(startDate, endDate);
        if (request.startDatePresent()) {
            classroom.setStartDate(startDate);
        }
        if (request.endDatePresent()) {
            classroom.setEndDate(endDate);
        }
        if (request.statusPresent()) {
            classroom.setStatus(requirePresent(request.status(), "status"));
        }
        classroomRepository.flush();
        return classroomMapper.toResponse(classroom);
    }

    private Classroom findClassroom(UUID id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Classroom"));
    }

    private Course findCourse(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Course"));
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw ApiExceptionFactory.badRequest("endDate must not be before startDate");
        }
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
