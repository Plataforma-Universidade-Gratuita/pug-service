CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    cpf        char(11)                 NOT NULL,
    name       varchar(150)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    UNIQUE (cpf)
);

CREATE INDEX idx_users_name ON users (name);
CREATE INDEX idx_users_cpf ON users (cpf);
