package br.org.catolicasc.pug.geo.infra.read;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
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
public interface CityQueries {

  /**
   * Retrieves a read-only view of a city based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the city to retrieve
   * @return an {@link Optional} containing the {@link CityView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<CityView> findOptionalById(UUID id);

  /**
   * Retrieves a read-only view of a city based on its unique IBGE code.
   *
   * @param ibgeCode the exact 7-digit IBGE code string
   * @return an {@link Optional} containing the {@link CityView} if found, or {@link
   *     Optional#empty()} otherwise
   */
  Optional<CityView> findOptionalByIbgeCode(String ibgeCode);

  /**
   * Retrieves a comprehensive list of all cities registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link CityView} entries
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
   * Executes a robust name-based search against city names.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., database-backed
   * filtering) to provide flexible matching, accent-insensitivity, and autocomplete
   * capabilities.
   *
   * @param key the raw search string or partial name provided by the user
   * @return a sorted {@link List} of {@link CityView} entries matching the search criteria, ordered
   *     by search relevance
   */
  List<CityView> searchByName(String key);
}
