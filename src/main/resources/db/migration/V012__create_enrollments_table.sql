CREATE TABLE enrollments
(
    project_id        uuid                     NOT NULL,
    student_id        uuid                     NOT NULL,
    status            varchar(16)              NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    accepted_at       TIMESTAMP WITH TIME ZONE,
    closing_status_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (project_id, student_id),
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (student_id) REFERENCES students (account_id),
    CONSTRAINT chk_accepted_consistency CHECK (accepted_at IS NULL OR accepted_at >= created_at),
    CONSTRAINT chk_closing_status_consistency CHECK (closing_status_at IS NULL OR closing_status_at >= created_at)
);

CREATE INDEX idx_enrollments_student ON enrollments (student_id);
CREATE INDEX idx_enrollments_status ON enrollments (status);
