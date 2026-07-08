package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import br.org.catolicasc.pug.identity.presenter.dtos.users.UserSimpleComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.util.UUID;

/**
 * Response DTO used as the content item returned by the account complex-search endpoint.
 *
 * @param id the unique identifier (UUIDv7) of the account
 * @param user the lightweight representation of the linked user
 * @param email the email address registered to the account
 * @param auditInfo the nested audit information containing creation and update timestamps
 * @param active flag indicating whether the account is currently active
 */
public record AccountComplexSearchResponse(
    UUID id,
    UserSimpleComplexSearchResponse user,
    String email,
    AccountTypeResponse accountType,
    AuditInfoResponse auditInfo,
    Boolean active) {}
