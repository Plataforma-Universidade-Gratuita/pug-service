--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

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

CREATE INDEX idx_courses_areaOfExpertise ON courses (area_of_expertise_id);
