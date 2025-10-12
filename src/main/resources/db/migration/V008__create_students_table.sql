CREATE TABLE students
(
    id                    uuid PRIMARY KEY,
    user_id               uuid          NOT NULL UNIQUE REFERENCES users (id),
    email                 varchar(254)  NOT NULL,
    academic_registration varchar(15)   NOT NULL UNIQUE,
    course_id             uuid          NOT NULL REFERENCES courses (id),
    required_hours        numeric(6, 2) NOT NULL,
    completed_hours       numeric(6, 2) NOT NULL DEFAULT 0,
    start_date            date          NOT NULL,
    due_date              date          NOT NULL,
    completed             boolean GENERATED ALWAYS AS (completed_hours >= required_hours) STORED,
    active                boolean       NOT NULL DEFAULT true,
    CONSTRAINT chk_students_dates CHECK (due_date >= start_date),
    CONSTRAINT chk_students_hours CHECK (
        required_hours >= 0 AND completed_hours >= 0 AND completed_hours <= required_hours
        ),
    CONSTRAINT chk_students_email_basic CHECK (position('@' in email) > 1)
);

CREATE UNIQUE INDEX uq_students_email_ci ON students (lower(email));
CREATE INDEX idx_students_course ON students (course_id);
CREATE INDEX idx_students_completed ON students (completed);
CREATE INDEX idx_students_window ON students (start_date, due_date);
