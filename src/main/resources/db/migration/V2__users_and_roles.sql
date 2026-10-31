CREATE TABLE roles (
    code VARCHAR(30) PRIMARY KEY,
    display_name VARCHAR(80) NOT NULL
);

INSERT INTO roles (code, display_name) VALUES
    ('STUDENT', 'Student'),
    ('ADMIN', 'Administrator'),
    ('SUPER_ADMIN', 'Super Administrator');

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_email_normalized CHECK (email = LOWER(email)),
    CONSTRAINT ck_users_status CHECK (status IN ('ACTIVE', 'DISABLED'))
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_code VARCHAR(30) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_code),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_code) REFERENCES roles (code)
);

CREATE INDEX ix_user_roles_role_code ON user_roles (role_code);
