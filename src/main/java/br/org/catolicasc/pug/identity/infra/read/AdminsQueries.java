package br.org.catolicasc.pug.identity.infra.read;

import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.service.dtos.AdminComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing administrator profile queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving consolidated administrator profiles directly into lightweight projections that join
 * Admin, Account, and User data as needed for the HTTP contract.
 */
public interface AdminsQueries {

  /**
   * Retrieves a read-only view of an administrator profile based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the admin's account
   * @return an {@link Optional} containing the {@link AdminView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<AdminView> findOptionalById(UUID accountId);

  /**
   * Retrieves a comprehensive list of all administrators registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link AdminView} entries available in the database
   */
  List<AdminView> listAllAdmins();

  /**
   * Retrieves the administrators whose linked account identifiers are present in the provided
   * collection.
   *
   * @param ids the account identifiers used to restrict the result set
   * @return a {@link List} containing the matching administrator projections
   */
  List<AdminView> listAllByIds(List<UUID> ids);

  /**
   * Executes paginated administrator search using the shared complex-search contract.
   *
   * @param pageQuery the requested pagination information
   * @param criteria the optional administrator search criteria
   * @return a paginated result containing the administrator search projections
   */
  PageResult<AdminComplexSearchView> search(
      PageQuery pageQuery, AdminComplexSearchCriteria criteria);
}
