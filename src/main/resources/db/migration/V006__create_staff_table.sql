--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE staff
(
    account_id uuid,
    entity_id  uuid NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts (id),
    FOREIGN KEY (entity_id) REFERENCES entities (id)
);

CREATE INDEX idx_staff_entity ON staff (entity_id);
