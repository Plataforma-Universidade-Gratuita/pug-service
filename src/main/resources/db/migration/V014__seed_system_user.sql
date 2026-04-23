WITH new_user AS (
-- 1. Create the base User record
INSERT
INTO users (id, cpf, name, created_at, updated_at)
VALUES (
    uuid_generate_v7(), '52998224725', 'System Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    )
    RETURNING id
    ), new_account AS (
-- 2. Create the Account record linked to the new User
INSERT
INTO accounts (id, user_id, email, account_type, password_hash, created_at, updated_at, active)
SELECT
    uuid_generate_v7(), id, 'admin@pug.com', 'ADMIN',
    -- Below is the BCrypt hash for the plain password 'admin' (without pepper).
    -- At startup, AdminPasswordSeeder re-hashes it with the configured pepper.
    '$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true
FROM new_user
    RETURNING id
    )
-- 3. Create the Admin role record linked to the new Account
INSERT
INTO admins (account_id, granted_at, campus)
SELECT id,
       CURRENT_TIMESTAMP,
       'JARAGUA_DO_SUL'
FROM new_account;