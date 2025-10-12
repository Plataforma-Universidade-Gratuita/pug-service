CREATE TABLE attendances
(
    id                 uuid PRIMARY KEY,
    enrollment_id      uuid          NOT NULL REFERENCES enrollments (id),
    duration           numeric(4, 2) NOT NULL,
    latitude           decimal(9, 6),
    longitude          decimal(9, 6),
    status             text          NOT NULL DEFAULT 'PENDING',
    qr_validation_hash varchar(512) UNIQUE,
    validated_by       uuid REFERENCES staff (id),
    validated_at       timestamptz,
    CONSTRAINT chk_attendance_duration_pos CHECK (duration > 0),
    CONSTRAINT chk_attendance_lat CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),
    CONSTRAINT chk_attendance_lon CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180)),
    CONSTRAINT chk_attendance_status CHECK (status IN ('PENDING', 'VALIDATED', 'REJECTED'))
);

CREATE INDEX idx_attendances_enrollment ON attendances (enrollment_id);
CREATE INDEX idx_attendances_status ON attendances (status);
CREATE INDEX idx_attendances_pending ON attendances (enrollment_id) WHERE status = 'PENDING';
CREATE INDEX idx_attendances_validated_by ON attendances (validated_by);
CREATE INDEX idx_attendances_validated_at ON attendances (validated_at);
