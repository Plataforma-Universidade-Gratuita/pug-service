CREATE TABLE courses
(
    id         uuid,
    name       varchar(150)             NOT NULL,
    area_of_expertise_id  uuid                     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (area_of_expertise_id) REFERENCES areas_of_expertise (id),
    UNIQUE (name)
);

CREATE INDEX idx_courses_school ON courses (area_of_expertise_id);
