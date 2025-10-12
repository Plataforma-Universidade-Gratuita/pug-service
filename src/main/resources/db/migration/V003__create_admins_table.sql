CREATE TABLE admins
(
    user_id    uuid PRIMARY KEY REFERENCES users (id),
    granted_at timestamptz NOT NULL DEFAULT now()
);
