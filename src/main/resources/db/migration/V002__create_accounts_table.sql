CREATE TABLE accounts
(
    id                  uuid PRIMARY KEY,
    person_id           uuid                     NOT NULL REFERENCES users (id),
    email               varchar(254)             NOT NULL,
    account_type        varchar(16)              NOT NULL,
    password_hash       varchar(255),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,

    UNIQUE (email)
);

CREATE INDEX idx_accounts_email ON accounts (email);
