CREATE TABLE students
(
    account_id            uuid,
    academic_registration varchar(15)              NOT NULL,
    campus                varchar(150)             NOT NULL,
    course_id             uuid                     NOT NULL,
    required_hours        DECIMAL(6, 2)            NOT NULL,
    start_date            date                     NOT NULL,
    due_date              date                     NOT NULL,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE (academic_registration),
    CONSTRAINT chk_students_dates CHECK (due_date >= start_date),
    CONSTRAINT chk_students_required_nonneg CHECK (required_hours >= 0)
);

CREATE INDEX idx_students_course ON students (course_id);
CREATE INDEX idx_students_window ON students (start_date, due_date);
