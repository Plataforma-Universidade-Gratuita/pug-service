package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;

/**
 * Response DTO that exposes account type information.
 *
 * @param accountType the account type used by the system for authorization and classification
 * @param accountTypeFormatted the localized, human-readable label for the account type
 */
public record AccountTypeResponse(
        AccountType accountType,
        String accountTypeFormatted
) {}