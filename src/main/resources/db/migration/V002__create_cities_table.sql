CREATE TABLE cities
(
    id        uuid PRIMARY KEY,
    name      varchar(100) NOT NULL,
    ibge_code char(7)      NOT NULL UNIQUE
);
