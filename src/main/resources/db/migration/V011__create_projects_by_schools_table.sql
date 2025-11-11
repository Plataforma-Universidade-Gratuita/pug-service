CREATE TABLE projects_by_schools
(
    project_id uuid NOT NULL REFERENCES projects (id),
    school_id  uuid NOT NULL REFERENCES schools (id),
    PRIMARY KEY (project_id, school_id)
);

CREATE INDEX idx_pbs_school ON projects_by_schools (school_id);
