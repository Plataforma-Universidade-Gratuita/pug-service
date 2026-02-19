CREATE TABLE attendances
(
    id                 uuid,
    project_id         uuid          NOT NULL,
    student_id         uuid          NOT NULL,
    duration           DECIMAL(4, 2) NOT NULL,
    latitude           DECIMAL(9, 6),
    longitude          DECIMAL(9, 6),
    status             varchar(16)   NOT NULL,
    qr_validation_hash varchar(512)  NOT NULL,
    validated_by       uuid,
    validated_at       TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (project_id, student_id) REFERENCES enrollments (project_id, student_id),
    FOREIGN KEY (validated_by) REFERENCES staff (account_id),
    UNIQUE (qr_validation_hash),
    CONSTRAINT chk_attendance_duration_pos CHECK (duration > 0),
    CONSTRAINT chk_attendance_lat CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),
    CONSTRAINT chk_attendance_lon CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180))
);

CREATE INDEX idx_attendances_enrollment ON attendances (project_id, student_id);
CREATE INDEX idx_attendances_status ON attendances (status);
CREATE INDEX idx_attendances_validated_by ON attendances (validated_by);
CREATE INDEX idx_attendances_validated_at ON attendances (validated_at);
CREATE INDEX idx_attendances_student_stat ON attendances (student_id, status);
