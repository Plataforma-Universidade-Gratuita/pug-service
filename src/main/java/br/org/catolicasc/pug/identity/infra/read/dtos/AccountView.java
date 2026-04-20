package br.org.catolicasc.pug.identity.infra.read.dtos;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an authentication Account.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It provides a lightweight projection of the account entity without nesting the associated
 * user details. Instead, it exposes only the {@code userId}, allowing clients to fetch user
 * information on demand via dedicated user endpoints.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param userId the unique identifier (UUIDv7) of the user linked to this account
 * @param email the email address registered for authentication
 * @param accountType the assigned role or classification of the account (e.g., ADMIN, STUDENT)
 * @param createdAt the exact timestamp when the account record was created
 * @param updatedAt the exact timestamp when the account record was last modified
 * @param active flag indicating whether the account is currently active
 */
public record AccountView(
    UUID id,
    UUID userId,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Boolean active) {}
