package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AccountResponse is a record that represents the response data for an account.
 *
 * @param id                   The unique identifier of the account.
 * @param user                 The user associated with the account.
 * @param email                The email address associated with the account.
 * @param accountType          The type of the account (e.g., ADMIN, USER).
 * @param accountTypeFormatted A formatted string representation of the account type.
 * @param createdAt            The date and time when the account was created.
 * @param createdAtFormatted   A formatted string representation of the creation date and time.
 * @param updatedAt            The date and time when the account was last updated.
 * @param updatedAtFormatted   A formatted string representation of the last update date and time.
 */
public record AccountResponse(
        UUID id,
        UserResponse user,
        String email,
        AccountType accountType,
        String accountTypeFormatted,
        OffsetDateTime createdAt,
        String createdAtFormatted,
        OffsetDateTime updatedAt,
        String updatedAtFormatted) {
}
