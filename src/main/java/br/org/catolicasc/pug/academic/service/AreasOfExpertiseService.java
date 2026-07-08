package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Command-side service contract for managing academic areas of expertise.
 *
 * <p>This boundary orchestrates lifecycle mutations for the area-of-expertise aggregate, including
 * creation, lookup for write flows, updates, and deletion. It preserves the public academic
 * nomenclature while leaving validation and persistence details to lower layers.
 */
public interface AreasOfExpertiseService {

  /**
   * Permanently deletes an area of expertise by its unique identifier.
   *
   * @param id the unique identifier of the area of expertise to delete
   * @return {@code true} when the area of expertise was deleted, or {@code false} when no matching
   *     record exists
   * @throws BusinessRuleException if the area of expertise is still linked to courses or project
   *     relationships that prevent deletion
   */
  boolean delete(UUID id);

  /**
   * Retrieves an area-of-expertise aggregate by its unique identifier.
   *
   * @param id the unique identifier of the area of expertise
   * @return the matching {@link AreaOfExpertise} aggregate
   * @throws ResourceNotFoundException if no area of expertise exists for the provided identifier
   * @throws AppValidationException if the persisted aggregate state violates domain invariants
   */
  AreaOfExpertise getById(UUID id);

  /**
   * Registers a new academic area of expertise.
   *
   * @param cmd the command containing the area-of-expertise creation data
   * @return the persisted {@link AreaOfExpertise} aggregate
   * @throws DuplicateResourceException if another area of expertise already uses the same name
   * @throws AppValidationException if the command produces an invalid aggregate state
   */
  AreaOfExpertise save(AreaOfExpertiseCreateCommand cmd);

  /**
   * Updates an existing academic area of expertise.
   *
   * @param id the unique identifier of the area of expertise to update
   * @param cmd the command containing the modified area-of-expertise data
   * @return the updated {@link AreaOfExpertise} aggregate
   * @throws ResourceNotFoundException if the target area of expertise does not exist
   * @throws DuplicateResourceException if the requested name conflicts with another record
   * @throws AppValidationException if the requested state violates domain invariants
   */
  AreaOfExpertise update(UUID id, AreaOfExpertiseUpdateCommand cmd);
}
