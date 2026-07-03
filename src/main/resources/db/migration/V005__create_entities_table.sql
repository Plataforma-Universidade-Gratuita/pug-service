--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE entities
(
    id         uuid,
    cnpj       char(14)                 NOT NULL,
    name       varchar(150)             NOT NULL,
    city_id    uuid                     NOT NULL,
    address    varchar(254),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (city_id) REFERENCES cities (id),
    UNIQUE (cnpj)
);

CREATE INDEX idx_entities_name ON entities (name);
CREATE INDEX idx_entities_city ON entities (city_id);
