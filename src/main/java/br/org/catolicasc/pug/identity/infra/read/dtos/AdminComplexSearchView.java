package br.org.catolicasc.pug.identity.infra.read.dtos;

import java.time.OffsetDateTime;

/**
 * Read-only projection used by the administrator complex-search flow.
 *
 * <p>This projection keeps the account-search payload nested as a lightweight account view while
 * adding the administrator-specific grant timestamp required by the frontend filtering contract.
 *
 * @param accountView the lightweight account projection associated with the administrator
 * @param grantedAt the exact timestamp when administrative privileges were granted
 */
public record AdminComplexSearchView(
    AccountComplexSearchView accountView, OffsetDateTime grantedAt) {}
