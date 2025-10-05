INSERT INTO cities (id, name, ibge_code)
VALUES ('0d8ba23a-c0b3-4cbf-90e7-937162a2ae9f', 'Jaraguá do Sul', '4208906'),
       ('11dbc2f6-25ef-416f-8112-f7b6796b561d', 'Joinville', '4209102'),
       ('c2aebd97-cea5-4a1c-b4df-8c1ceec90460', 'Florianópolis', '4205407'),
       ('7455bfdb-cf83-4ca2-9ec8-bcbcacefb37b', 'Guaramirim', '4206504'),
       ('2813707d-2591-4c9f-ba6a-bf8100a15a8c', 'Araquari', '4201307'),
       ('19bfbf59-a2f7-4736-a145-2d95422fd62b', 'Blumenau', '4202404'),
       ('51d9902d-8643-43c8-a870-1506c2533feb', 'Schroeder', '4217402'),
       ('3caf1389-a2ee-4e01-89d8-0a7083f34ce4', 'Corupá', '4204509');

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
