CREATE TABLE cities
(
    id        uuid,
    name      varchar(150) NOT NULL,
    ibge_code char(7)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (ibge_code)
);
