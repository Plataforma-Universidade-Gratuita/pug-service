CREATE TABLE fields_of_study
(
    id   uuid PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
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

DROP INDEX IF EXISTS idx_fields_of_study_unaccent_name;
CREATE INDEX idx_fields_of_study_unaccent_name
    ON fields_of_study (immutable_unaccent(lower(name)));