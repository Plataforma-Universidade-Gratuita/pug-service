CREATE TABLE students
(
    user_id               uuid PRIMARY KEY REFERENCES users (id),
    academic_registration varchar(15)   NOT NULL UNIQUE,
    campus                varchar(150)  NOT NULL,
    course_id             uuid          NOT NULL REFERENCES courses (id),
    required_hours        DECIMAL(6, 2) NOT NULL,
    start_date            date          NOT NULL,
    due_date              date          NOT NULL,

    CONSTRAINT chk_students_dates CHECK (due_date >= start_date),
    CONSTRAINT chk_students_required_nonneg CHECK (required_hours >= 0)
);

CREATE INDEX idx_students_course ON students (course_id);
CREATE INDEX idx_students_window ON students (start_date, due_date);
