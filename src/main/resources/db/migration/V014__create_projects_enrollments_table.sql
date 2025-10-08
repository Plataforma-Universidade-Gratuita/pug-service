CREATE TABLE projects_enrollments
(
    id                uuid PRIMARY KEY,
    project_id        uuid        NOT NULL REFERENCES projects (id),
    student_id        uuid        NOT NULL REFERENCES students (id),
    status            varchar(50) NOT NULL DEFAULT 'PENDING',
    request_at        timestamptz NOT NULL DEFAULT now(),
    accepted_at       timestamptz,
    closing_status_at timestamptz
);

CREATE INDEX idx_proj_enroll_project ON projects_enrollments (project_id);
CREATE INDEX idx_proj_enroll_student ON projects_enrollments (student_id);
CREATE INDEX idx_proj_enroll_status ON projects_enrollments (status);
