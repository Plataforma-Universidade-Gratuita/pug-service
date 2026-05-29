package br.org.catolicasc.pug.geo.service;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Geographic data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and instantiates lightweight {@link CityView} Data Transfer Objects directly from
 * the underlying data store.
 */
public interface CitiesReadService {

  /**
   * Retrieves a read-only projection of a city based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested city
   * @return the populated {@link CityView} DTO
   * @throws ResourceNotFoundException if no city matches the provided ID
   */
  CityView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all cities registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link CityView} entries
   */
  List<CityView> listViews();

  /**
   * Retrieves a list of city projections based on a provided list of unique identifiers.
   *
   * <p>This method is optimized for batch retrieval scenarios, allowing clients to fetch multiple
   * city views in a single call. The results are returned in the same order as the input IDs.
   *
   * @param ids a {@link List} of unique identifiers (UUIDs) corresponding to the desired cities
   * @return a {@link List} of {@link CityView} entries matching the provided IDs
   */
  List<CityView> listViewsByIds(List<UUID> ids);

  /**
   * Executes paginated city search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link CityView} entries
   */
  PageResult<CityView> search(PageQuery pageQuery, CityComplexSearchCriteria criteria);
}
