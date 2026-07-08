package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Account data.
 *
 * <p>This record provides the client with the essential details of an authentication account in a
 * flattened shape. Instead of nesting the full user payload, it exposes only the {@code userId},
 * allowing clients to retrieve detailed user information on demand via the appropriate user
 * endpoints.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param userId the unique identifier (UUIDv7) of the user linked to this account
 * @param email the email address registered to the account
 * @param accountType the nested account type information containing formatted value
 * @param auditInfo the nested audit information containing creation and update timestamps
 * @param active flag indicating whether the account is currently active
 */
public record AccountResponse(
    UUID id,
    UUID userId,
    String email,
    AccountTypeResponse accountType,
    AuditInfoResponse auditInfo,
    Boolean active) {}
