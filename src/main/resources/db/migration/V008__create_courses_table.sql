CREATE TABLE courses
(
    id         uuid,
    name       varchar(150)             NOT NULL,
    school_id  uuid                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (school_id) REFERENCES schools (id),
    UNIQUE (name)
);

CREATE INDEX idx_courses_school ON courses (school_id);
