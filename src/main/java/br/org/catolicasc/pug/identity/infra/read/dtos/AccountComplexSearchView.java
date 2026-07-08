package br.org.catolicasc.pug.identity.infra.read.dtos;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Read-only projection used by the account complex-search flow.
 *
 * <p>This projection joins account data with the minimal linked-user fields required by the search
 * response, allowing the database query to resolve both filtering and response assembly in a single
 * round-trip.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param userId the unique identifier (UUIDv7) of the linked user
 * @param userName the full name of the linked user
 * @param email the email address registered to the account
 * @param accountType the designated role or classification of the account
 * @param createdAt the exact timestamp when the account record was created
 * @param updatedAt the exact timestamp when the account record was last modified
 * @param active flag indicating whether the account is currently active
 */
public record AccountComplexSearchView(
    UUID id,
    UUID userId,
    String userName,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    Boolean active) {}
