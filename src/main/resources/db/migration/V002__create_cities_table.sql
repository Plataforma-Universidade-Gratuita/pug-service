CREATE TABLE cities
(
    id        uuid PRIMARY KEY,
    name      varchar(100) NOT NULL UNIQUE,
    ibge_code char(7)      NOT NULL UNIQUE,
    CONSTRAINT chk_cities_ibge_digits CHECK (ibge_code ~ '^[0-9]{7}$')
);
