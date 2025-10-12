CREATE TABLE enrollments
(
    id                uuid PRIMARY KEY,
    allocation_id     uuid        NOT NULL REFERENCES allocations (id),
    student_id        uuid        NOT NULL REFERENCES students (id),
    status            text        NOT NULL DEFAULT 'PENDING',
    request_at        timestamptz NOT NULL DEFAULT now(),
    accepted_at       timestamptz,
    closing_status_at timestamptz,
    CONSTRAINT chk_enrollments_unique UNIQUE (allocation_id, student_id),
    CONSTRAINT chk_enroll_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_enroll_times CHECK (
        (accepted_at IS NULL OR accepted_at >= request_at) AND
        (closing_status_at IS NULL OR closing_status_at >= request_at)
        )
);

CREATE INDEX idx_enrollments_allocation ON enrollments (allocation_id);
CREATE INDEX idx_enrollments_student ON enrollments (student_id);
CREATE INDEX idx_enrollments_status ON enrollments (status);
CREATE INDEX idx_enrollments_pending ON enrollments (allocation_id) WHERE status = 'PENDING';
CREATE INDEX idx_enrollments_alloc_active_status ON enrollments (allocation_id) WHERE status IN ('ACCEPTED','COMPLETED');

