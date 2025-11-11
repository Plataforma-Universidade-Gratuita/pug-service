CREATE TABLE students
(
    account_id            uuid PRIMARY KEY REFERENCES accounts (id),
    academic_registration varchar(15)   NOT NULL UNIQUE,
    campus                varchar(150)  NOT NULL,
    course_id             uuid          NOT NULL REFERENCES courses (id),
    required_hours        DECIMAL(6, 2) NOT NULL,
    completed_hours       DECIMAL(6, 2) NOT NULL,
    start_date            date          NOT NULL,
    due_date              date          NOT NULL,

    CONSTRAINT chk_students_dates CHECK (due_date >= start_date),
    CONSTRAINT chk_students_required_nonneg CHECK (required_hours >= 0),
    CONSTRAINT chk_students_completed_nonneg CHECK (completed_hours >= 0),
    CONSTRAINT chk_students_completed_lte_required CHECK (completed_hours <= required_hours)
);

CREATE INDEX idx_students_course ON students (course_id);
CREATE INDEX idx_students_window ON students (start_date, due_date);
