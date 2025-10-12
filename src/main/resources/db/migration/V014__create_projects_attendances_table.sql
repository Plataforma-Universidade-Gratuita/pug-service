CREATE TABLE projects_attendances
(
    id                  uuid PRIMARY KEY,
    enrollment_id       uuid          NOT NULL REFERENCES projects_enrollments (id),
    project_location_id uuid          NOT NULL REFERENCES projects_locations (id),
    duration            numeric(4, 2) NOT NULL,
    latitude            decimal(9, 6),
    longitude           decimal(9, 6),
    status              varchar(50)            DEFAULT 'PENDING',
    qr_validation_hash  varchar(512) UNIQUE,
    validated_by        uuid REFERENCES staff (id),
    validated_at        timestamptz,
    created_at          timestamptz   NOT NULL DEFAULT now(),
    updated_at          timestamptz
);

CREATE INDEX idx_projects_attendances_enrollment ON projects_attendances (enrollment_id);
CREATE INDEX idx_projects_attendances_location ON projects_attendances (project_location_id);
CREATE INDEX idx_projects_attendances_status ON projects_attendances (status);
