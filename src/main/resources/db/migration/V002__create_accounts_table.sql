CREATE TABLE accounts
(
    id            uuid,
    user_id       uuid                     NOT NULL,
    email         varchar(254)             NOT NULL,
    account_type  varchar(16)              NOT NULL,
    password_hash varchar(255)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    UNIQUE (email)
);
