CREATE TABLE courses
(
    id        uuid PRIMARY KEY,
    name      varchar(120) NOT NULL UNIQUE,
    school_id uuid         NOT NULL REFERENCES schools (id)
);

CREATE INDEX idx_courses_school ON courses (school_id);
