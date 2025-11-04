CREATE TABLE enrollments
(
    project_id        uuid                     NOT NULL REFERENCES projects (id),
    student_id        uuid                     NOT NULL REFERENCES students (user_id),
    status            varchar(16)              NOT NULL,
    request_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at       TIMESTAMP WITH TIME ZONE,
    closing_status_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT pk_enrollments PRIMARY KEY (project_id, student_id),
    CONSTRAINT chk_enroll_times CHECK (
        (accepted_at IS NULL OR accepted_at >= request_at) AND
        (closing_status_at IS NULL OR closing_status_at >= request_at)
        )
);

CREATE INDEX idx_enrollments_student ON enrollments (student_id);
CREATE INDEX idx_enrollments_status ON enrollments (status);
CREATE INDEX idx_enrollments_project ON enrollments (project_id);
