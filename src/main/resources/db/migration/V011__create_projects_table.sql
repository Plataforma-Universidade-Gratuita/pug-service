CREATE TABLE projects
(
    id               uuid PRIMARY KEY,
    name             varchar(150) NOT NULL,
    description      text,
    entity_id        uuid         NOT NULL REFERENCES entities (id),
    field_id         uuid         NOT NULL REFERENCES fields_of_study (id),
    status           varchar(50)  NOT NULL,
    max_participants integer,
    created_by       uuid         NOT NULL REFERENCES staff (id),
    created_at       timestamptz  NOT NULL DEFAULT now(),
    updated_by       uuid         NOT NULL REFERENCES staff (id),
    updated_at       timestamptz
);

CREATE INDEX idx_projects_entity ON projects (entity_id);
CREATE INDEX idx_projects_field ON projects (field_id);
CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_created_by ON projects (created_by);
CREATE INDEX idx_projects_updated_by ON projects (updated_by);

CREATE INDEX idx_projects_unaccent_name
    ON cities (immutable_unaccent(lower(name)));
