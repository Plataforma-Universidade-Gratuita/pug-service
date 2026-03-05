package com.pug.identity.infra.read.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an authentication Account.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It aggregates the account details with a nested projection of the associated user ({@link
 * UserView}) to provide a comprehensive, lightweight structure optimized for JSON serialization.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param user the read-only projection of the user linked to this account
 * @param email the email address registered for authentication
 * @param accountType the assigned role or classification of the account (e.g., ADMIN, STUDENT)
 * @param createdAt the exact timestamp when the account record was created
 * @param updatedAt the exact timestamp when the account record was last modified
 */
public record AccountView(
    UUID id,
    UserView user,
    String email,
    AccountType accountType,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
