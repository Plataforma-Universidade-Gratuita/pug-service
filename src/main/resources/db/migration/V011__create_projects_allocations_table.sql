CREATE TABLE projects_allocations
(
    id            uuid PRIMARY KEY,
    project_id    uuid          NOT NULL REFERENCES projects (id),
    offered_hours numeric(6, 2) NOT NULL,
    start_date    date          NOT NULL,
    end_date      date          NOT NULL,
    CONSTRAINT chk_proj_alloc_dates CHECK (end_date >= start_date)
);

CREATE INDEX idx_proj_alloc_project ON projects_allocations (project_id);
