CREATE TABLE cities
(
    id        uuid PRIMARY KEY,
    name      varchar(100) NOT NULL UNIQUE,
    ibge_code varchar(7)   NOT NULL UNIQUE
);

CREATE INDEX idx_cities_unaccent_name
    ON cities (immutable_unaccent(lower(name)));
