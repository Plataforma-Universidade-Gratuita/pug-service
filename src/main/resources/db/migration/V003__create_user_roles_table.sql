CREATE TABLE users_roles
(
    id         uuid PRIMARY KEY,
    user_id    uuid         NOT NULL REFERENCES users (id),
    role       varchar(50)  NOT NULL,
    email      varchar(254) NOT NULL UNIQUE,
    active     boolean      NOT NULL DEFAULT true,
    created_at timestamptz  NOT NULL DEFAULT now(),
    updated_at timestamptz
);

CREATE INDEX idx_users_roles_user ON users_roles (user_id);
CREATE INDEX idx_users_roles_role ON users_roles (role);

CREATE INDEX idx_users_roles_unaccent_email
    ON users_roles (immutable_unaccent(lower(email)));