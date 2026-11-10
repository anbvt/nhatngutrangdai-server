CREATE SEQUENCE invoice_number_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE invoices (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    enrollment_id UUID,
    invoice_number VARCHAR(40) NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE,
    currency VARCHAR(3) NOT NULL,
    total_amount NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_invoices_invoice_number UNIQUE (invoice_number),
    CONSTRAINT ck_invoices_currency CHECK (length(currency) = 3),
    CONSTRAINT ck_invoices_total_positive CHECK (total_amount > 0),
    CONSTRAINT ck_invoices_due_date CHECK (due_date IS NULL OR due_date >= issue_date),
    CONSTRAINT fk_invoices_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_invoices_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments (id)
);

CREATE INDEX ix_invoices_student_id ON invoices (student_id);
CREATE INDEX ix_invoices_issue_date ON invoices (issue_date);

CREATE TABLE invoice_items (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    line_number INTEGER NOT NULL,
    description VARCHAR(500) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_amount NUMERIC(12, 2) NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    CONSTRAINT uq_invoice_items_line UNIQUE (invoice_id, line_number),
    CONSTRAINT ck_invoice_items_line_number CHECK (line_number > 0),
    CONSTRAINT ck_invoice_items_quantity CHECK (quantity > 0),
    CONSTRAINT ck_invoice_items_unit_amount CHECK (unit_amount >= 0),
    CONSTRAINT ck_invoice_items_line_total CHECK (line_total >= 0 AND line_total = quantity * unit_amount),
    CONSTRAINT fk_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    method VARCHAR(30) NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE NOT NULL,
    external_reference VARCHAR(120),
    note VARCHAR(1000),
    recorded_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_payments_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_payments_currency CHECK (length(currency) = 3),
    CONSTRAINT ck_payments_method CHECK (method IN ('CASH', 'BANK_TRANSFER', 'CARD', 'OTHER')),
    CONSTRAINT fk_payments_invoice FOREIGN KEY (invoice_id) REFERENCES invoices (id),
    CONSTRAINT fk_payments_recorded_by FOREIGN KEY (recorded_by) REFERENCES users (id)
);

CREATE INDEX ix_payments_invoice_id ON payments (invoice_id);
CREATE INDEX ix_payments_paid_at ON payments (paid_at);

CREATE TABLE financial_transactions (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    actor_id UUID NOT NULL,
    reference VARCHAR(120),
    CONSTRAINT uq_financial_transactions_payment UNIQUE (payment_id),
    CONSTRAINT ck_financial_transactions_type CHECK (transaction_type = 'PAYMENT'),
    CONSTRAINT ck_financial_transactions_amount_positive CHECK (amount > 0),
    CONSTRAINT fk_financial_transactions_payment FOREIGN KEY (payment_id) REFERENCES payments (id),
    CONSTRAINT fk_financial_transactions_actor FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX ix_financial_transactions_occurred_at ON financial_transactions (occurred_at);

CREATE TABLE financial_audit_events (
    id UUID PRIMARY KEY,
    actor_id UUID NOT NULL,
    action VARCHAR(40) NOT NULL,
    resource_type VARCHAR(30) NOT NULL,
    resource_id UUID NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_financial_audit_actor FOREIGN KEY (actor_id) REFERENCES users (id)
);

CREATE INDEX ix_financial_audit_resource
    ON financial_audit_events (resource_type, resource_id, occurred_at);
