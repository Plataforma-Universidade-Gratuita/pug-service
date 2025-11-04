CREATE TABLE users
(
    id                  uuid PRIMARY KEY,
    cpf                 char(11)                 NOT NULL,
    name                varchar(150)             NOT NULL,
    email               varchar(254)             NOT NULL,
    account_type        varchar(16)              NOT NULL,
    password_hash       varchar(255),
    active              boolean                  NOT NULL DEFAULT true,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,

    UNIQUE (email),
    UNIQUE (cpf, account_type, email)
);

CREATE INDEX idx_users_name ON users (name);
CREATE INDEX idx_users_cpf ON users (cpf);
