CREATE TABLE projects_locations
(
    id                    uuid PRIMARY KEY,
    project_allocation_id uuid NOT NULL REFERENCES projects_allocations (id),
    address               varchar(254),
    latitude              decimal(9, 6),
    longitude             decimal(9, 6)
);

CREATE INDEX idx_proj_loc_allocation ON projects_locations (project_allocation_id);
