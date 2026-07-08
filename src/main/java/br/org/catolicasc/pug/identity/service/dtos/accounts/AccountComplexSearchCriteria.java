package br.org.catolicasc.pug.identity.service.dtos.accounts;

import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Internal search criteria used by the account read-side complex-search flow.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param accountTypes optional account types used in an {@code in} filter
 * @param dateFrom optional lower-bound timestamp applied inclusively to supported audit timestamps
 * @param dateTo optional upper-bound timestamp applied inclusively to supported audit timestamps
 * @param activeOnly flag indicating whether only active accounts should be returned
 */
public record AccountComplexSearchCriteria(
    String name,
    String cpf,
    String email,
    List<AccountType> accountTypes,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    boolean activeOnly) {

  /**
   * Creates immutable internal criteria for account complex-search execution.
   *
   * <p>The {@code accountTypes} collection is defensively copied to preserve immutability across
   * service and repository boundaries.
   */
  public AccountComplexSearchCriteria {
    accountTypes = accountTypes == null ? List.of() : List.copyOf(accountTypes);
  }
}
