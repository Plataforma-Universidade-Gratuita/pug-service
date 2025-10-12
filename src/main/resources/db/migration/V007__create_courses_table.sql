CREATE TABLE courses
(
    id         uuid PRIMARY KEY,
    name       varchar(120) NOT NULL UNIQUE,
    field_id   uuid         NOT NULL REFERENCES fields_of_study (id),
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz
);

CREATE INDEX idx_courses_field ON courses (field_id);
CREATE INDEX idx_courses_name  ON courses (name);
