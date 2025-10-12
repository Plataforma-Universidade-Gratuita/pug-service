CREATE TABLE allocations
(
    id               uuid PRIMARY KEY,
    project_id       uuid          NOT NULL REFERENCES projects (id),
    offered_hours    numeric(6, 2) NOT NULL,
    allocated_hours  numeric(6, 2) NOT NULL,
    status           text          NOT NULL,
    start_date       date          NOT NULL,
    end_date         date          NOT NULL,
    created_by       uuid          NOT NULL REFERENCES staff (id),
    created_at       timestamptz   NOT NULL DEFAULT now(),
    completed_at     timestamptz,
    max_participants integer,
    period daterange GENERATED ALWAYS AS (daterange(start_date, end_date, '[]')) STORED,
    CONSTRAINT chk_proj_alloc_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_proj_alloc_hours CHECK (offered_hours >= 0 AND allocated_hours >= 0),
    CONSTRAINT chk_alloc_hours_le CHECK (allocated_hours <= offered_hours),
    CONSTRAINT chk_alloc_status CHECK (status IN ('DRAFT', 'OPEN', 'CLOSED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_alloc_max_part CHECK (max_participants IS NULL OR max_participants >= 0)
);

CREATE INDEX idx_allocations_project ON allocations (project_id);
CREATE INDEX idx_allocations_status ON allocations (status);
CREATE INDEX idx_allocations_created_by ON allocations (created_by);
CREATE INDEX idx_allocations_created_at ON allocations (created_at);
CREATE INDEX idx_allocations_completed_at ON allocations (completed_at);
CREATE INDEX idx_allocations_period_gist ON allocations USING gist (period);
