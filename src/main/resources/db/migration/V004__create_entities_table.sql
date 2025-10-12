CREATE TABLE entities
(
    id         uuid PRIMARY KEY,
    cnpj       varchar(14)  NOT NULL UNIQUE,
    name       varchar(150) NOT NULL,
    city_id    uuid         NOT NULL REFERENCES cities (id),
    address    varchar(254),
    active     boolean      NOT NULL DEFAULT true,
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz
);

CREATE INDEX idx_entities_city    ON entities (city_id);
CREATE INDEX idx_entities_name    ON entities (name);
CREATE INDEX idx_entities_address ON entities (address);