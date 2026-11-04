CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    student_id UUID,
    course_id UUID NOT NULL,
    application_reference_hash VARCHAR(64) NOT NULL,
    applicant_full_name VARCHAR(160) NOT NULL,
    applicant_email VARCHAR(254) NOT NULL,
    applicant_email_normalized VARCHAR(254) NOT NULL,
    applicant_phone VARCHAR(40) NOT NULL,
    applicant_date_of_birth DATE,
    applicant_address VARCHAR(500),
    applicant_nationality VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    pending_duplicate_key VARCHAR(320),
    submitted_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_enrollments_reference_hash UNIQUE (application_reference_hash),
    CONSTRAINT uq_enrollments_pending_duplicate_key UNIQUE (pending_duplicate_key),
    CONSTRAINT ck_enrollments_status CHECK (status IN (
        'PENDING', 'APPROVED', 'CONFIRMED', 'COMPLETED', 'REJECTED', 'WAITLISTED', 'CANCELLED'
    )),
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE INDEX ix_enrollments_status ON enrollments (status);
CREATE INDEX ix_enrollments_course_id ON enrollments (course_id);
CREATE INDEX ix_enrollments_submitted_at ON enrollments (submitted_at);

CREATE TABLE enrollment_status_history (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    actor_id UUID,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    note VARCHAR(1000),
    CONSTRAINT ck_enrollment_history_old_status CHECK (
        old_status IS NULL OR old_status IN (
            'PENDING', 'APPROVED', 'CONFIRMED', 'COMPLETED', 'REJECTED', 'WAITLISTED', 'CANCELLED'
        )
    ),
    CONSTRAINT ck_enrollment_history_new_status CHECK (new_status IN (
        'PENDING', 'APPROVED', 'CONFIRMED', 'COMPLETED', 'REJECTED', 'WAITLISTED', 'CANCELLED'
    )),
    CONSTRAINT fk_enrollment_history_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments (id),
    CONSTRAINT fk_enrollment_history_actor FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX ix_enrollment_history_enrollment_occurred
    ON enrollment_status_history (enrollment_id, occurred_at);
