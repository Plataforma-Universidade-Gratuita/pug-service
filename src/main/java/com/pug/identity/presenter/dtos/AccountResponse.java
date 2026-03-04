package com.pug.identity.presenter.dtos;

import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.presenter.dtos.AuditInfoResponse;

import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Account data.
 * <p>
 * This record provides the client with the essential details of an authentication account,
 * including nested user data and formatted strings optimized for direct UI presentation.
 *
 * @param id                   the unique identifier (UUIDv7) of the account
 * @param user                 the nested, client-facing projection of the associated user
 * @param email                the email address registered to the account
 * @param accountType          the designated role or classification of the account (e.g., ADMIN, STUDENT)
 * @param accountTypeFormatted a localized, human-readable string representation of the account type
 * @param auditInfo            the nested audit information containing creation and update timestamps
 */
public record AccountResponse(
        UUID id,
        UserResponse user,
        String email,
        AccountType accountType,
        String accountTypeFormatted,
        AuditInfoResponse auditInfo) {
}