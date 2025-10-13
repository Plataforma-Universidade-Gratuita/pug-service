CREATE TABLE staff
(
    id        uuid PRIMARY KEY,
    user_id   uuid         NOT NULL REFERENCES users (id),
    email     varchar(254) NOT NULL,
    entity_id uuid         NOT NULL REFERENCES entities (id),
    active    boolean      NOT NULL DEFAULT true,
    CONSTRAINT chk_staff_email_basic CHECK (position('@' in email) > 1)
);

CREATE UNIQUE INDEX uq_staff_email_ci ON staff (lower(email));
CREATE INDEX idx_staff_user ON staff (user_id);
CREATE INDEX idx_staff_entity ON staff (entity_id);
CREATE INDEX idx_staff_entity_active ON staff (entity_id) WHERE active;
