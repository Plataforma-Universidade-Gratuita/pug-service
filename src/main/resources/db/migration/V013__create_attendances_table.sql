--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE attendances
(
    id                 uuid,
    project_id         uuid          NOT NULL,
    former_student_id         uuid          NOT NULL,
    duration           DECIMAL(4, 2) NOT NULL,
    status             varchar(16)   NOT NULL,
    qr_validation_hash varchar(512)  NOT NULL,
    validated_by       uuid,
    validated_at       TIMESTAMP WITH TIME ZONE,
    created_at         TIMESTAMP WITH TIME ZONE,
    updated_at         TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (project_id, former_student_id) REFERENCES enrollments (project_id, former_student_id),
    FOREIGN KEY (validated_by) REFERENCES accounts (id),
    UNIQUE (qr_validation_hash),
    CONSTRAINT chk_attendance_duration_pos CHECK (duration > 0)
);

CREATE INDEX idx_attendances_enrollment ON attendances (project_id, former_student_id);
CREATE INDEX idx_attendances_status ON attendances (status);
CREATE INDEX idx_attendances_validated_by ON attendances (validated_by);
CREATE INDEX idx_attendances_validated_at ON attendances (validated_at);
CREATE INDEX idx_attendances_former_student_stat ON attendances (former_student_id, status);
