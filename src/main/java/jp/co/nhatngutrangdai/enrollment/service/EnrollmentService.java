package jp.co.nhatngutrangdai.enrollment.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import jp.co.nhatngutrangdai.common.api.PageResponse;
import jp.co.nhatngutrangdai.common.exception.ApiExceptionFactory;
import jp.co.nhatngutrangdai.common.persistence.PageableSupport;
import jp.co.nhatngutrangdai.course.entity.Course;
import jp.co.nhatngutrangdai.course.repository.CourseRepository;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentAdminResponse;
import jp.co.nhatngutrangdai.enrollment.dto.EnrollmentDetailResponse;
import jp.co.nhatngutrangdai.enrollment.dto.PublicEnrollmentApplicationRequest;
import jp.co.nhatngutrangdai.enrollment.dto.PublicEnrollmentApplicationResponse;
import jp.co.nhatngutrangdai.enrollment.entity.Enrollment;
import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatus;
import jp.co.nhatngutrangdai.enrollment.entity.EnrollmentStatusHistory;
import jp.co.nhatngutrangdai.enrollment.mapper.EnrollmentMapper;
import jp.co.nhatngutrangdai.enrollment.repository.EnrollmentRepository;
import jp.co.nhatngutrangdai.enrollment.repository.EnrollmentSpecifications;
import jp.co.nhatngutrangdai.enrollment.repository.EnrollmentStatusHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private static final Set<String> SORT_FIELDS = Set.of(
            "applicantFullName", "status", "submittedAt", "createdAt");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentStatusHistoryRepository historyRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final Clock clock;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            EnrollmentStatusHistoryRepository historyRepository,
            CourseRepository courseRepository,
            EnrollmentMapper enrollmentMapper,
            Clock clock) {
        this.enrollmentRepository = enrollmentRepository;
        this.historyRepository = historyRepository;
        this.courseRepository = courseRepository;
        this.enrollmentMapper = enrollmentMapper;
        this.clock = clock;
    }

    @Transactional
    public PublicEnrollmentApplicationResponse submit(PublicEnrollmentApplicationRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> ApiExceptionFactory.notFound("Course"));
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        String duplicateKey = request.courseId() + ":" + normalizedEmail;
        if (enrollmentRepository.existsByPendingDuplicateKey(duplicateKey)) {
            throw ApiExceptionFactory.conflict(
                    "A pending application already exists for this email and course");
        }

        String reference = createReference();
        Instant submittedAt = clock.instant();
        Enrollment enrollment = Enrollment.pendingApplication(
                course,
                hashReference(reference),
                request.fullName(),
                request.email(),
                normalizedEmail,
                request.phone(),
                request.dateOfBirth(),
                request.address(),
                request.nationality(),
                duplicateKey,
                submittedAt);
        Enrollment saved = enrollmentRepository.saveAndFlush(enrollment);
        historyRepository.save(EnrollmentStatusHistory.initialSubmission(saved, submittedAt));
        return enrollmentMapper.toPublicResponse(saved, reference);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentAdminResponse> list(
            EnrollmentStatus status,
            UUID courseId,
            Pageable pageable) {
        Pageable safePageable = PageableSupport.validated(pageable, SORT_FIELDS, "submittedAt");
        return enrollmentRepository.findAll(
                EnrollmentSpecifications.filter(status, courseId),
                safePageable).map(enrollmentMapper::toAdminResponse);
    }

    @Transactional(readOnly = true)
    public EnrollmentDetailResponse get(UUID id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> ApiExceptionFactory.notFound("Enrollment"));
        return enrollmentMapper.toDetailResponse(
                enrollment,
                historyRepository.findAllByEnrollment_IdOrderByOccurredAtAsc(id));
    }

    private String createReference() {
        byte[] random = new byte[32];
        SECURE_RANDOM.nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }

    private String hashReference(String reference) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(reference.getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is required for application references", exception);
        }
    }
}
