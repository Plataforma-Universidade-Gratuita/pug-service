package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * UserResponse record.
 *
 * @param id the user ID
 * @param person the personal details of the user
 * @param email the email of the user
 * @param accountType the type of account
 * @param accountTypeFormatted the formatted text for the account type
 * @param createdAt the creation date and time
 * @param createdAtFormatted the formatted text for the creation date and time
 */
public record AccountResponse(
    UUID id,
    UserResponse person,
    String email,
    AccountType accountType,
    String accountTypeFormatted,
    OffsetDateTime createdAt,
    String createdAtFormatted) {}
