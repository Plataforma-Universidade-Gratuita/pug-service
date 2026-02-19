CREATE TABLE staff
(
    account_id uuid,
    entity_id  uuid,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (entity_id) REFERENCES entities (id)
);

CREATE INDEX idx_staff_entity ON staff (entity_id);
