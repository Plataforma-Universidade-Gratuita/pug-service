CREATE TABLE cities
(
    id        uuid,
    name      varchar(150) NOT NULL,
    ibge_code char(7)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (ibge_code)
);

INSERT INTO cities (id, name, ibge_code)
VALUES (uuid_generate_v7(), 'Jaraguá do Sul', '4205407'),
       (uuid_generate_v7(), 'Joinville', '4209106');

CREATE
OR REPLACE FUNCTION prevent_default_cities_modification()
RETURNS TRIGGER AS $$
BEGIN
    IF
OLD.ibge_code IN ('4205407', '4209106') THEN
        RAISE EXCEPTION 'Action not allowed: Jaraguá do Sul and Joinville are default system cities and cannot be changed or deleted.';
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_protect_default_cities
    BEFORE UPDATE OR
DELETE
ON cities
FOR EACH ROW
EXECUTE FUNCTION prevent_default_cities_modification();