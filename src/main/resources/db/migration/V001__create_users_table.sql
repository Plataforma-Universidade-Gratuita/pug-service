CREATE TABLE users
(
    id   uuid PRIMARY KEY,
    cpf  char(11)     NOT NULL UNIQUE,
    name varchar(150) NOT NULL,
    CONSTRAINT chk_users_cpf_digits CHECK (cpf ~ '^[0-9]{11}$')
);

CREATE INDEX idx_users_name ON users (name);
