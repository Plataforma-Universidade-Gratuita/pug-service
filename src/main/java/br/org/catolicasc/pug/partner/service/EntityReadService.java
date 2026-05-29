package br.org.catolicasc.pug.partner.service;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Partner Entity data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight, fully resolved {@link EntityView} Data Transfer Objects
 * directly from the underlying data store or search indices.
 */
public interface EntityReadService {

  /**
   * Retrieves a read-only projection of a partner entity based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the partner entity
   * @return the populated {@link EntityView} DTO
   * @throws ResourceNotFoundException if no entity matches the provided ID
   */
  EntityView getViewById(UUID id);

  /**
   * Retrieves a read-only projection of a partner entity based on its exact CNPJ.
   *
   * @param cnpj the raw 14-digit numeric CNPJ string
   * @return the populated {@link EntityView} DTO
   * @throws ResourceNotFoundException if no entity matches the provided CNPJ
   */
  EntityView getViewByCnpj(String cnpj);

  /**
   * Retrieves a filtered list of city views representing only the cities currently linked to
   * entities.
   *
   * <p>This method determines which cities are currently in use by fetching all distinct city
   * identifiers associated with persisted partner entities, and subsequently filtering the
   * comprehensive list of cities provided by the geographical module.
   *
   * @return a {@link List} of {@link CityView} containing the details of actively used cities.
   */
  List<CityView> listCityViews();

  /**
   * Retrieves a comprehensive list of all partner entities registered in the system.
   *
   * <p><i>Note:</i> This method returns the entire dataset. It should be used judiciously in
   * contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link EntityView} entries
   */
  List<EntityView> listViews();

  /**
   * Retrieves a list of partner entities geographically located within a specific city.
   *
   * @param cityId the unique identifier (UUID) of the city
   * @return a {@link List} of matching {@link EntityView} entries
   */
  List<EntityView> listViewsByCityId(UUID cityId);

  /**
   * Executes a robust name-based search against the names of partner organizations.
   *
   * <p>Leverages database-backed filtering (e.g., database-backed filtering) to provide fuzzy
   * matching, accent-insensitivity, and name matching.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link EntityView} entries
   */
  List<EntityView> searchViews(String query);
}
