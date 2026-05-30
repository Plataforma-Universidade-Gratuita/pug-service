package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import java.util.UUID;

/**
 * Command-side service interface for managing academic areas of expertise.
 *
 * <p>This service exposes operations to create, retrieve, update, and delete academic areas of
 * expertise while preserving the public nomenclature used by the application API.
 */
public interface AreasOfExpertiseService {

  /**
   * Permanently deletes an area of expertise by its unique identifier.
   *
   * @param id the unique identifier of the area of expertise to delete
   * @return {@code true} when the area of expertise was deleted, or {@code false} when no matching
   *     record exists
   */
  boolean delete(UUID id);

  /**
   * Retrieves an area-of-expertise aggregate by its unique identifier.
   *
   * @param id the unique identifier of the area of expertise
   * @return the matching {@link School} aggregate
   */
  School getById(UUID id);

  /**
   * Registers a new academic area of expertise.
   *
   * @param cmd the command containing the area-of-expertise creation data
   * @return the persisted {@link School} aggregate
   */
  School save(AreaOfExpertiseCreateCommand cmd);

  /**
   * Updates an existing academic area of expertise.
   *
   * @param id the unique identifier of the area of expertise to update
   * @param cmd the command containing the modified area-of-expertise data
   * @return the updated {@link School} aggregate
   */
  School update(UUID id, AreaOfExpertiseUpdateCommand cmd);
}
