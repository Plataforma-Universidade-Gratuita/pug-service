package br.org.catolicasc.pug.identity.infra.read;

import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.dtos.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing user queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving user data directly into lightweight {@link UserView} projections, bypassing the
 * overhead of instantiating full JPA entities or domain aggregates.
 */
public interface UsersQueries {

  /**
   * Retrieves a read-only view of a user based on their unique identifier.
   *
   * @param id the unique identifier (UUID) of the user to retrieve
   * @return an {@link Optional} containing the {@link UserView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<UserView> findOptionalById(UUID id);

  /**
   * Retrieves user projections for a provided collection of unique identifiers.
   *
   * <p>This query supports batch lookup scenarios where multiple users must be resolved in a single
   * round-trip to the persistence layer. Only users matching the supplied identifiers are returned.
   *
   * @param ids a {@link List} of user identifiers to resolve
   * @return a {@link List} containing the matching {@link UserView} projections
   */
  List<UserView> listAllByIds(List<UUID> ids);

  /**
   * Retrieves a comprehensive list of all users registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link UserView} entries available in the database
   */
  List<UserView> listAllUsers();

  /**
   * Executes paginated user search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link UserView} entries
   */
  PageResult<UserView> search(PageQuery pageQuery, UserComplexSearchCriteria criteria);
}
