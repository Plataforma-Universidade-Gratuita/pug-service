package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AccountResponse record.
 *
 * @param id                   the account ID
 * @param user                 the user details associated with this account (as a UserResponse)
 * @param email                the email of the account
 * @param accountType          the type of account
 * @param accountTypeFormatted the formatted text for the account type (localized)
 * @param createdAt            the creation date and time
 * @param createdAtFormatted   the formatted text for the creation date and time (localized)
 */
public record AccountResponse(
        UUID id,
        UserResponse user,
        String email,
        AccountType accountType,
        String accountTypeFormatted,
        OffsetDateTime createdAt,
        String createdAtFormatted) {
}
