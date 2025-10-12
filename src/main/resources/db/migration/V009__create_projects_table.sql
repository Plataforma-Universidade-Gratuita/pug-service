CREATE TABLE projects
(
    id          uuid PRIMARY KEY,
    name        varchar(150) NOT NULL,
    entity_id   uuid         NOT NULL REFERENCES entities (id),
    description text         NOT NULL,
    status      text         NOT NULL DEFAULT 'OPEN',
    created_at  timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT chk_projects_status CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED', 'ARCHIVED')),
    CONSTRAINT uq_projects_entity_name UNIQUE (entity_id, name)
);

CREATE INDEX idx_projects_entity ON projects (entity_id);
CREATE INDEX idx_projects_entity_status ON projects (entity_id, status);
CREATE INDEX idx_projects_created_at ON projects (created_at);
