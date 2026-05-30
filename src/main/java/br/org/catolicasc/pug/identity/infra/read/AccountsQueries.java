package br.org.catolicasc.pug.identity.infra.read;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing account queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving authentication account data directly into lightweight read-model projections,
 * bypassing the overhead of instantiating full JPA entities or domain aggregates.
 */
public interface AccountsQueries {

  /**
   * Retrieves a read-only view of an account based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the account to retrieve
   * @return an {@link Optional} containing the {@link AccountView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AccountView> findOptionalById(UUID id);

  /**
   * Retrieves account projections for a provided collection of unique identifiers.
   *
   * <p>This query supports batch lookup scenarios where multiple accounts must be resolved in a
   * single round-trip to the persistence layer. Only accounts matching the supplied identifiers are
   * returned.
   *
   * @param ids a {@link List} of account identifiers to resolve
   * @return a {@link List} containing the matching {@link AccountView} projections
   */
  List<AccountView> listAllByIds(List<UUID> ids);

  /**
   * Retrieves a comprehensive list of all accounts registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link AccountView} entries available in the database
   */
  List<AccountView> listAllAccounts();

  /**
   * Executes paginated account search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link AccountComplexSearchView} entries
   */
  PageResult<AccountComplexSearchView> search(
      PageQuery pageQuery, AccountComplexSearchCriteria criteria);
}
