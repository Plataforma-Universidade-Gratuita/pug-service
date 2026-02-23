package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AccountResponse is a record that represents the response data for an account.
 *
 * @param id The unique identifier of the account.
 * @param user The account associated with the account.
 * @param email The email address associated with the account.
 * @param accountType The type of the account (e.g., ADMIN, USER).
 * @param accountTypeFormatted A formatted string representation of the account type.
 * @param auditInfo The creation and update info.
 */
public record AccountResponse(
        UUID id,
        UserResponse user,
        String email,
        AccountType accountType,
        String accountTypeFormatted,
        AuditInfoResponse auditInfo) {}
