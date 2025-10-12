CREATE TABLE students
(
    id                    uuid PRIMARY KEY,
    user_role_id          uuid        NOT NULL UNIQUE REFERENCES users_roles (id),
    academic_registration varchar(15) NOT NULL UNIQUE,
    course_id             uuid        NOT NULL REFERENCES courses (id)
);

CREATE INDEX idx_students_course ON students (course_id);