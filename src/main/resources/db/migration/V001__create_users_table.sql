CREATE TABLE users
(
    id         uuid,
    cpf        char(11)                 NOT NULL,
    name       varchar(150)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (cpf)
);

CREATE INDEX idx_users_name ON users (name);
