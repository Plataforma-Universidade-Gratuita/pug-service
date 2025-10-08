CREATE TABLE projects_locations
(
    id                    uuid PRIMARY KEY,
    project_allocation_id uuid        NOT NULL REFERENCES projects_allocations (id) ON DELETE CASCADE,
    address               varchar(254),
    latitude              decimal(9, 6),
    longitude             decimal(9, 6),
    created_at            timestamptz NOT NULL DEFAULT now(),
    updated_at            timestamptz,
    CONSTRAINT chk_proj_loc_lat_range CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),
    CONSTRAINT chk_proj_loc_lng_range CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180)),
    CONSTRAINT chk_proj_loc_latlng_pair CHECK ((latitude IS NULL) = (longitude IS NULL))
);

CREATE INDEX idx_proj_loc_allocation ON projects_locations (project_allocation_id);

CREATE INDEX idx_projects_locations_unaccent_address
    ON projects_locations (immutable_unaccent(lower(address)));

