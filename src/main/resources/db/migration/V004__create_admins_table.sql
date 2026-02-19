CREATE TABLE admins
(
    account_id uuid,
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (account_id),
    FOREIGN KEY (account_id) REFERENCES accounts (id)
);
