package br.org.catolicasc.pug.identity.presenter.dtos;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Response DTO used as the content item returned by the account complex-search endpoint.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param user the lightweight representation of the linked user
 * @param email the email address registered to the account
 * @param accountType the designated role or classification of the account
 * @param accountTypeFormatted a localized, human-readable string representation of the account type
 * @param auditInfo the nested audit information containing creation and update timestamps
 * @param active flag indicating whether the account is currently active
 */
public record AccountComplexSearchResponse(
    UUID id,
    UserComplexSearchResponse user,
    String email,
    AccountType accountType,
    String accountTypeFormatted,
    AuditInfoResponse auditInfo,
    Boolean active) {}
