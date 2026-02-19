CREATE TABLE projects_by_schools
(
    project_id uuid NOT NULL,
    school_id  uuid NOT NULL,
    PRIMARY KEY (project_id, school_id),
    FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    FOREIGN KEY (school_id) REFERENCES schools (id) ON DELETE CASCADE
);

CREATE INDEX idx_pbs_school ON projects_by_schools (school_id);
