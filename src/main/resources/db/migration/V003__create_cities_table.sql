CREATE TABLE cities
(
    id        uuid PRIMARY KEY,
    name      varchar(100) NOT NULL UNIQUE,
    ibge_code varchar(7)   NOT NULL UNIQUE
);

CREATE
EXTENSION IF NOT EXISTS unaccent;

CREATE
OR REPLACE FUNCTION immutable_unaccent(text)
RETURNS text
LANGUAGE sql
IMMUTABLE
AS $$
SELECT unaccent('unaccent', $1) $$;

DROP INDEX IF EXISTS idx_cities_unaccent_name;
CREATE INDEX idx_cities_unaccent_name
    ON cities (immutable_unaccent(lower(name)));