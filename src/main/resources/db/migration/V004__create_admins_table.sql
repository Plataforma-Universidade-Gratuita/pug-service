CREATE TABLE admins
(
    account_id uuid PRIMARY KEY REFERENCES accounts (id),
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL
);
