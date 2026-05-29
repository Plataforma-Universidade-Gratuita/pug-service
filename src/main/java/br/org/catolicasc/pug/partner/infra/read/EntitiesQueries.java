package br.org.catolicasc.pug.partner.infra.read;

import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.service.dtos.EntityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against partner entities.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving partner-organization data directly into lightweight projections, bypassing the
 * overhead of instantiating full JPA entities or domain aggregates.
 */
public interface EntitiesQueries {

  /**
   * Retrieves a read-only view of a partner entity based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the entity to find
   * @return an {@link Optional} containing the found {@link EntityView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<EntityView> findOptionalById(UUID id);

  /**
   * Retrieves partner-entity projections for the provided unique identifiers in a single batch.
   *
   * @param ids the identifiers that should be resolved
   * @return the matching {@link EntityView} entries ordered by entity name
   */
  List<EntityView> listAllByIds(List<UUID> ids);

  /**
   * Retrieves a comprehensive list of all partner entities registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link EntityView} objects
   */
  List<EntityView> listAllEntities();

  /**
   * Executes the paginated partner-entity complex-search flow.
   *
   * @param pageQuery the shared pagination contract
   * @param criteria the optional complex-search criteria combined with logical {@code AND}
   * @return the paginated complex-search projections
   */
  PageResult<EntityComplexSearchView> search(
      PageQuery pageQuery, EntityComplexSearchCriteria criteria);
}
