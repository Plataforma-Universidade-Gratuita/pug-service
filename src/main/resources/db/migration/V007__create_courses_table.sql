CREATE TABLE courses
(
    id         uuid PRIMARY KEY,
    name       varchar(120) NOT NULL,
    field_id   uuid         NOT NULL REFERENCES fields_of_study (id),
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz
);

CREATE INDEX idx_courses_field ON courses (field_id);