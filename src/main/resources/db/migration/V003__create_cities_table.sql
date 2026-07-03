--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE cities
(
    id        uuid,
    name      varchar(150) NOT NULL,
    ibge_code char(7)      NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (ibge_code)
);
