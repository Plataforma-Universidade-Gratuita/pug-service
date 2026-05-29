package br.org.catolicasc.pug.partner.infra.read;

import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Partner Entities.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving partner organization data directly into lightweight {@link EntityView} projections,
 * bypassing the overhead of instantiating full JPA entities or domain aggregates.
 */
public interface EntityQueries {

  /**
   * Retrieves a read-only view of a partner entity based on its exact CNPJ.
   *
   * @param cnpj the exact 14-digit numeric CNPJ string to find
   * @return an {@link Optional} containing the found {@link EntityView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<EntityView> findOptionalByCnpj(String cnpj);

  /**
   * Retrieves a read-only view of a partner entity based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the entity to find
   * @return an {@link Optional} containing the found {@link EntityView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<EntityView> findOptionalById(UUID id);

  /**
   * Retrieves a list of partner entities located in a specific city.
   *
   * @param cityId the unique identifier (UUID) of the city
   * @return a {@link List} of {@link EntityView} objects located in the specified city
   */
  List<EntityView> listAllByCityId(UUID cityId);

  /**
   * Retrieves a list of all city identifiers that are currently associated with any partner entity.
   *
   * <p>This method is useful for determining which cities are actively in use by the partner
   * entities, allowing clients to filter or display only relevant geographic options.
   *
   * @return a {@link List} of {@link UUID} representing the unique identifiers of cities linked to
   *     partner entities
   */
  List<UUID> listAllCityIds();

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
   * Executes a robust name-based search against the names of partner entities.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., database-backed
   * filtering).
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of {@link EntityView} entries matching the search criteria
   */
  List<EntityView> searchByName(String query);
}
