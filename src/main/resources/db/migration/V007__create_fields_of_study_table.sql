CREATE TABLE fields_of_study
(
    id   uuid PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
);

CREATE INDEX idx_fields_of_study_unaccent_name
    ON fields_of_study (immutable_unaccent(lower(name)));
