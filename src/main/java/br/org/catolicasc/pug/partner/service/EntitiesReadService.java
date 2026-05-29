package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.partner.service.dtos.EntityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying partner-entity data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight read projections directly from the underlying data store.
 */
public interface EntitiesReadService {

  /**
   * Retrieves a read-only projection of a partner entity based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the partner entity
   * @return the populated {@link EntityView} DTO
   * @throws ResourceNotFoundException if no entity matches the provided ID
   */
  EntityView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of partner entities registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link EntityView} entries
   */
  List<EntityView> listViews();

  /**
   * Retrieves a batch of partner-entity projections resolved by their unique identifiers.
   *
   * @param ids the identifiers that should be resolved in a single read operation
   * @return the matching {@link EntityView} projections, ordered by entity name
   */
  List<EntityView> listViewsByIds(List<UUID> ids);

  /**
   * Executes the paginated partner-entity complex-search flow.
   *
   * @param pageQuery the shared pagination request contract
   * @param criteria the optional search criteria combined with logical {@code AND}
   * @return the paginated complex-search result set
   */
  PageResult<EntityComplexSearchView> search(
      PageQuery pageQuery, EntityComplexSearchCriteria criteria);
}
