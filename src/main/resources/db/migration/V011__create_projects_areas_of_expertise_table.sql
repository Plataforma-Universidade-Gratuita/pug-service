--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE project_areas_of_expertise
(
    project_id uuid NOT NULL,
    area_of_expertise_id  uuid NOT NULL,
    PRIMARY KEY (project_id, area_of_expertise_id),
    FOREIGN KEY (project_id) REFERENCES projects (id),
    FOREIGN KEY (area_of_expertise_id) REFERENCES areas_of_expertise (id)
);

CREATE INDEX idx_pbs_areas_of_expertise ON project_areas_of_expertise (area_of_expertise_id);
