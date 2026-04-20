package br.org.catolicasc.pug.academic.infra.read;

import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Academic Schools.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture. It defines operations for
 * retrieving school data directly into lightweight {@link SchoolView} projections, bypassing the
 * overhead of instantiating full JPA entities.
 */
public interface SchoolQueries {

  /**
   * Retrieves a read-only view of a school based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the school to find
   * @return an {@link Optional} containing the found {@link SchoolView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<SchoolView> findOptionalById(UUID id);

  /**
   * Retrieves a comprehensive list of all academic schools registered in the system.
   *
   * <p><i>Note:</i> Use with caution if the dataset grows significantly, as this method does not
   * implement pagination.
   *
   * @return a {@link List} of all {@link SchoolView} objects
   */
  List<SchoolView> listAllSchools();

  /**
   * Retrieves a list of schools corresponding to the provided list of unique identifiers.
   *
   * <p>This method is optimized for batch retrieval and may leverage database-specific features
   * (e.g., SQL IN clause) for efficient querying.
   *
   * @param ids a {@link List} of UUIDs representing the unique identifiers of the schools to
   *     retrieve
   * @return a {@link List} of {@link SchoolView} objects matching the provided IDs
   */
  List<SchoolView> listByIds(List<UUID> ids);

  /**
   * Executes a robust full-text search against the names of schools.
   *
   * <p>This method typically leverages underlying indexing engines (e.g., Elasticsearch via
   * Hibernate Search).
   *
   * @param key the raw search string or partial name provided by the client
   * @return a sorted {@link List} of {@link SchoolView} entries matching the search criteria
   */
  List<SchoolView> searchByName(String key);
}
