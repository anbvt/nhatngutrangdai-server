CREATE TABLE students (
    id UUID PRIMARY KEY,
    user_id UUID,
    student_code VARCHAR(40) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    date_of_birth DATE,
    email VARCHAR(254),
    phone VARCHAR(40),
    address VARCHAR(500),
    nationality VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_students_user_id UNIQUE (user_id),
    CONSTRAINT uq_students_student_code UNIQUE (student_code),
    CONSTRAINT ck_students_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX ix_students_full_name ON students (full_name);
CREATE INDEX ix_students_status ON students (status);

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(2000),
    level VARCHAR(80),
    duration VARCHAR(120),
    tuition_amount NUMERIC(12, 2),
    currency VARCHAR(3),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_courses_tuition_non_negative CHECK (tuition_amount IS NULL OR tuition_amount >= 0),
    CONSTRAINT ck_courses_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX ix_courses_code ON courses (code);
CREATE INDEX ix_courses_name ON courses (name);
CREATE INDEX ix_courses_status ON courses (status);

CREATE TABLE teachers (
    id UUID PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(254),
    phone VARCHAR(40),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_teachers_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX ix_teachers_full_name ON teachers (full_name);
CREATE INDEX ix_teachers_status ON teachers (status);

CREATE TABLE classrooms (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(160) NOT NULL,
    capacity INTEGER NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_classrooms_capacity_positive CHECK (capacity > 0),
    CONSTRAINT ck_classrooms_date_order CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date),
    CONSTRAINT ck_classrooms_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT fk_classrooms_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE INDEX ix_classrooms_course_id ON classrooms (course_id);
CREATE INDEX ix_classrooms_code ON classrooms (code);
CREATE INDEX ix_classrooms_status ON classrooms (status);
