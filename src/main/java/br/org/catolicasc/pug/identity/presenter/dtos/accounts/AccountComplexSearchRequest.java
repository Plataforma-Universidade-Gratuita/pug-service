/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.presenter.dtos.accounts;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Request DTO used by the account complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies all of them
 * using logical {@code AND}. Timestamp filters are evaluated against every audit timestamp field
 * involved in the account search query, including both account and linked-user audit fields.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param accountTypes optional account types used in an {@code in} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported audit timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported audit timestamps
 * @param activeOnly optional flag indicating whether only active accounts should be returned;
 *     defaults to {@code true} when omitted
 */
public record AccountComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String name,
    @Pattern(regexp = ".*\\S.*") String cpf,
    @Pattern(regexp = ".*\\S.*") String email,
    List<AccountType> accountTypes,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    Boolean activeOnly) {

  /**
   * Creates an immutable complex-search request payload for account queries.
   *
   * <p>The {@code accountTypes} collection is defensively copied to prevent accidental mutation
   * after request instantiation.
   */
  public AccountComplexSearchRequest {
    accountTypes = accountTypes == null ? List.of() : List.copyOf(accountTypes);
  }
}
