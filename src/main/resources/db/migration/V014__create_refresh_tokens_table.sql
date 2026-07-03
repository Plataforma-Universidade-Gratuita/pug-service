--
-- Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
-- All rights reserved.
--
-- This database migration is proprietary and confidential. Unauthorized use,
-- copying, modification, distribution, or deployment is prohibited.
--

CREATE TABLE refresh_tokens
(
    id         uuid                     DEFAULT uuid_generate_v7(),
    account_id uuid                     NOT NULL,
    token_hash varchar(128)             NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE CASCADE,
    UNIQUE (token_hash)
);

CREATE INDEX idx_refresh_tokens_account_id ON refresh_tokens (account_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);

