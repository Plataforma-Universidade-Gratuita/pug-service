CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    cpf        varchar(11)  NOT NULL UNIQUE,
    name       varchar(150) NOT NULL,
    created_at timestamp    NOT NULL DEFAULT now(),
    updated_at timestamp
);

CREATE INDEX idx_users_name ON users (name);