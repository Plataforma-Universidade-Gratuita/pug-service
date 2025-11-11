CREATE TABLE staff
(
    account_id uuid PRIMARY KEY REFERENCES accounts (id),
    entity_id  uuid NOT NULL REFERENCES entities (id)
);

CREATE INDEX idx_staff_entity ON staff (entity_id);
