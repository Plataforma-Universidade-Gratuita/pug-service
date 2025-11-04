CREATE TABLE projects
(
    id                 uuid PRIMARY KEY,
    name               varchar(150)             NOT NULL,
    entity_id          uuid                     NOT NULL REFERENCES entities (id),
    description        varchar(4000)            NOT NULL,
    created_by         uuid                     NOT NULL REFERENCES staff (user_id),
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    closed_at          TIMESTAMP WITH TIME ZONE,
    offered_hours      DECIMAL(6, 2)            NOT NULL,
    status             varchar(16)              NOT NULL,
    max_participants   integer,

    CONSTRAINT uq_projects_entity_name UNIQUE (entity_id, name),
    CONSTRAINT chk_proj_hours_pos CHECK (offered_hours >= 0),
    CONSTRAINT chk_proj_max_part CHECK (max_participants IS NULL OR max_participants >= 0)
);

CREATE INDEX idx_projects_entity ON projects (entity_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_created_by ON projects (created_by);
CREATE INDEX idx_projects_created_at ON projects (created_at);
CREATE INDEX idx_projects_closed_at ON projects (closed_at);
