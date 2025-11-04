CREATE TABLE admins
(
    user_id    uuid PRIMARY KEY REFERENCES users (id),
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL
);
