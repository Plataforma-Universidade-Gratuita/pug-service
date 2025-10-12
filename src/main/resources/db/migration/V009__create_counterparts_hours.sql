CREATE TABLE counterparts_hours
(
    id             uuid PRIMARY KEY,
    student_id     uuid          NOT NULL REFERENCES students (id),
    required_hours numeric(6, 2) NOT NULL,
    start_date     date          NOT NULL,
    due_date       date          NOT NULL,
    CONSTRAINT chk_sch_dates CHECK (due_date >= start_date)
);

CREATE INDEX idx_sch_student ON counterparts_hours (student_id);