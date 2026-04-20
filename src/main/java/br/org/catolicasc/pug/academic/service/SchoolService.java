package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.SchoolCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.SchoolUpdateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link School} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It orchestrates domain logic, enforces cross-cutting
 * business constraints (e.g., ensuring school names are unique), and coordinates with the
 * persistence layer.
 */
public interface SchoolService {

  /**
   * Removes a {@link School} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the school to delete
   * @return {@code true} if the school was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID id);

  /**
   * Retrieves a full {@link School} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration (e.g.,
   * verifying a school exists before linking a Course). For API responses, use {@link
   * SchoolReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the school
   * @return the fully reconstituted {@link School} aggregate
   * @throws ResourceNotFoundException if the school does not exist
   * @throws AppValidationException if the school exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  School getById(UUID id);

  /**
   * Instantiates and persists a new {@link School} aggregate based on the provided command.
   *
   * @param cmd the structured command containing the data to create the new school
   * @return the fully instantiated and persisted {@link School} aggregate
   * @throws DuplicateResourceException if a school with the exact same
   *     name already exists
   * @throws AppValidationException if input validation fails
   */
  School save(SchoolCreateCommand cmd);

  /**
   * Updates the state of an existing {@link School} aggregate using the provided data.
   *
   * <p>This method applies partial updates and enforces uniqueness constraints on the updated name
   * before persisting the changes.
   *
   * @param id the unique identifier (UUIDv7) of the school to update
   * @param cmd the structured command containing the new data for the school
   * @return the mutated and persisted {@link School} aggregate
   * @throws ResourceNotFoundException if the school does not exist
   * @throws DuplicateResourceException if the updated name conflicts with
   *     an existing school
   * @throws AppValidationException if the updated input data violates
   *     domain constraints
   */
  School update(UUID id, SchoolUpdateCommand cmd);
}
