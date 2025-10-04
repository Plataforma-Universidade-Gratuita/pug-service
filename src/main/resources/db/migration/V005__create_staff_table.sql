CREATE TABLE staff
(
    id           uuid PRIMARY KEY,
    user_role_id uuid NOT NULL UNIQUE REFERENCES users_roles (id),
    entity_id    uuid NOT NULL REFERENCES entities (id)
);

CREATE INDEX idx_staff_entity ON staff (entity_id);
