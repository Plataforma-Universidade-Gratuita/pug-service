CREATE TABLE former_students
(
    account_id            uuid,
    academic_registration varchar(15)              NOT NULL,
    campus                varchar(150)             NOT NULL,
    course_id             uuid                     NOT NULL,
    required_hours        DECIMAL(6, 2)            NOT NULL,
    completed_hours       DECIMAL(6, 2) NOT NULL DEFAULT 0.00,
    start_date            date                     NOT NULL,
    due_date              date                     NOT NULL,
    concluded             boolean                  NOT NULL DEFAULT FALSE,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (course_id) REFERENCES courses (id),
    UNIQUE (academic_registration),
    CONSTRAINT chk_former_students_dates CHECK (due_date >= start_date),
    CONSTRAINT chk_former_students_required_nonneg CHECK (required_hours > 0),
    CONSTRAINT chk_former_students_completed_hours_nonneg CHECK (completed_hours >= 0)
);

CREATE INDEX idx_former_students_course ON former_students (course_id);
CREATE INDEX idx_former_students_window ON former_students (start_date, due_date);
