CREATE TABLE staff
(
    user_id   uuid PRIMARY KEY REFERENCES users (id),
    entity_id uuid NOT NULL REFERENCES entities (id)
);

CREATE INDEX idx_staff_entity ON staff (entity_id);
