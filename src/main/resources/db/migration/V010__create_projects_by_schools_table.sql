CREATE TABLE projects_by_schools
(
    id         uuid PRIMARY KEY,
    project_id uuid NOT NULL REFERENCES projects (id),
    school_id  uuid NOT NULL REFERENCES schools (id),
    CONSTRAINT chk_pbs_unique UNIQUE (project_id, school_id)
);

CREATE INDEX idx_pbs_project ON projects_by_schools (project_id);
CREATE INDEX idx_pbs_school ON projects_by_schools (school_id);
