CREATE TABLE project_areas_of_expertise
(
    project_id uuid NOT NULL,
    area_of_expertise_id  uuid NOT NULL,
    PRIMARY KEY (project_id, area_of_expertise_id),
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (area_of_expertise_id) REFERENCES areas_of_expertises (id)
);

CREATE INDEX idx_pbs_areas_of_expertise ON project_areas_of_expertise (area_of_expertise_id);
