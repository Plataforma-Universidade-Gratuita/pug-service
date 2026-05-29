package br.org.catolicasc.pug.geo.infra.read;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing geographic queries.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving geographic data directly into lightweight {@link CityView} projections, bypassing the
 * overhead of instantiating full JPA entities or domain aggregates.
 */
public interface CitiesQueries {

  /**
   * Retrieves a read-only view of a city based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the city to retrieve
   * @return an {@link Optional} containing the {@link CityView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<CityView> findOptionalById(UUID id);

  /**
   * Retrieves city projections for a provided collection of unique identifiers.
   *
   * <p>This query supports batch lookup scenarios where multiple cities must be resolved in a
   * single round-trip to the persistence layer. Only cities matching the supplied identifiers are
   * returned.
   *
   * @param ids a {@link List} of city identifiers to resolve
   * @return a {@link List} containing the matching {@link CityView} projections
   */
  List<CityView> listAllByIds(List<UUID> ids);

  /**
   * Retrieves a comprehensive list of all cities registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link CityView} entries available in the database
   */
  List<CityView> listAllCities();

  /**
   * Executes paginated city search using the provided page request and complex-search criteria.
   *
   * @param pageQuery the requested page and page size
   * @param criteria the optional search criteria
   * @return a paginated result of matching {@link CityView} entries
   */
  PageResult<CityView> search(PageQuery pageQuery, CityComplexSearchCriteria criteria);
}
