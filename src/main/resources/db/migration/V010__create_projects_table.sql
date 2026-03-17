CREATE TABLE projects
(
    id               uuid,
    name             varchar(150)             NOT NULL,
    entity_id        uuid                     NOT NULL,
    description      varchar(4000)            NOT NULL,
    created_by       uuid                     NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    closed_at        TIMESTAMP WITH TIME ZONE,
    offered_hours    DECIMAL(6, 2)            NOT NULL,
    status           varchar(16)              NOT NULL,
    max_participants integer,
    PRIMARY KEY (id),
    FOREIGN KEY (entity_id) REFERENCES entities (id),
    FOREIGN KEY (created_by) REFERENCES accounts (id),
    UNIQUE (entity_id, name),
    CONSTRAINT chk_proj_hours_pos CHECK (offered_hours >= 0),
    CONSTRAINT chk_proj_max_part CHECK (max_participants IS NULL OR max_participants >= 0)
);

CREATE INDEX idx_projects_entity ON projects (entity_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_created_by ON projects (created_by);
CREATE INDEX idx_projects_created_at ON projects (created_at);
CREATE INDEX idx_projects_updated_at ON projects (updated_at);
CREATE INDEX idx_projects_closed_at ON projects (closed_at);
