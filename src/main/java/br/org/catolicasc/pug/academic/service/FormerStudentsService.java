package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link FormerStudent} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete). It orchestrates the complex lifecycle relationship between a FormerStudent enrollment,
 * their underlying {@link Account}, and the {@link Course} they are enrolled in, ensuring that
 * academic records and authentication credentials cascade correctly.
 */
public interface FormerStudentsService {

  /**
   * Adds completed hours to a formerStudent's academic progress.
   *
   * <p>This method updates the formerStudent's record, recalculating whether the formerStudent has
   * reached the total required counterpart hours.
   *
   * @param accountId the formerStudent's account identifier
   * @param hours the amount of hours to add
   * @return the updated {@link FormerStudent} aggregate
   * @throws ResourceNotFoundException if the formerStudent does not exist
   */
  FormerStudent addCompletedHours(UUID accountId, BigDecimal hours);

  /**
   * Removes a formerStudent's enrollment by deleting the {@link FormerStudent} record.
   *
   * <p>This operation enforces data hygiene. After the academic enrollment is successfully removed,
   * the service automatically triggers the deletion of the underlying {@link Account} to ensure
   * credentials tied strictly to formerStudent roles are wiped out.
   *
   * @param accountId the unique identifier of the FormerStudent to delete (Account ID)
   * @return {@code true} if the formerStudent was successfully deleted, {@code false} if they were
   *     not found
   */
  boolean delete(UUID accountId);

  /**
   * Checks whether any active formerStudent enrollment associated with a specific course exists.
   *
   * <p>This method is utilized to enforce relational integrity, ensuring that an academic {@link
   * Course} cannot be deleted if it still has enrolled former students.
   *
   * @param courseId the unique identifier (UUID) of the course to check
   * @return {@code true} if at least one formerStudent is enrolled in the course, {@code false}
   *     otherwise
   */
  boolean existsAnyByCourseId(UUID courseId);

  /**
   * Retrieves a full {@link FormerStudent} domain aggregate by its linked account identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link FormerStudentsReadService#getViewByAccountId(UUID)} instead.
   *
   * @param accountId the unique identifier (UUID) of the linked account
   * @return the fully reconstituted {@link FormerStudent} aggregate
   * @throws ResourceNotFoundException if the formerStudent does not exist
   * @throws AppValidationException if the formerStudent exists but its stored state violates strict
   *     domain invariants (data corruption)
   */
  FormerStudent getById(UUID accountId);

  /**
   * Instantiates and persists a new {@link FormerStudent} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It verifies that the specified course exists, then
   * delegates the creation of the underlying authentication account (and potentially the user
   * identity) to the {@link AccountsService} before saving the formerStudent's academic enrollment
   * records.
   *
   * @param cmd the structured command containing the data to create the formerStudent and linked
   *     account
   * @return the fully instantiated and persisted {@link FormerStudent} aggregate
   * @throws DuplicateResourceException if the academic registration or account email already exists
   * @throws ResourceNotFoundException if the associated course does not exist
   * @throws AppValidationException if input validation fails at the domain level
   */
  FormerStudent save(FormerStudentCreateCommand cmd);

  /**
   * Instantiates and persists multiple {@link FormerStudent} aggregates in a single batch
   * transaction.
   *
   * <p>This method iterates over the provided commands, applying the exact same domain rules,
   * validations, and cascading identity provisions as a single creation, ensuring that the entire
   * batch operation succeeds or fails together.
   *
   * @param cmds a {@link List} of structured commands containing the data to create former-student
   *     records
   * @return a {@link List} of the fully instantiated and persisted {@link FormerStudent} aggregates
   * @throws DuplicateResourceException if any academic registration or email already exists
   * @throws ResourceNotFoundException if any associated course does not exist
   * @throws AppValidationException if input validation fails for any record
   */
  List<FormerStudent> saveInBulk(List<FormerStudentCreateCommand> cmds);

  /**
   * Updates an existing {@link FormerStudent} and optionally its underlying account using the
   * provided data.
   *
   * <p>This method applies partial updates. If account data is provided in the command, the update
   * is cascaded down to the underlying account aggregate. If a new course ID is provided, the
   * service verifies its existence before changing the formerStudent's enrollment.
   *
   * @param accountId the unique identifier of the FormerStudent (which corresponds directly to the
   *     Account ID)
   * @param cmd the structured command containing the new data for the formerStudent and/or account
   * @return the mutated and persisted {@link FormerStudent} aggregate
   * @throws ResourceNotFoundException if the formerStudent does not exist, or the new course is not
   *     found
   * @throws DuplicateResourceException if the updated academic registration or email conflicts with
   *     existing records
   * @throws AppValidationException if input validation fails
   */
  FormerStudent update(UUID accountId, FormerStudentUpdateCommand cmd);

  /**
   * Updates the activation status of the linked former-student account.
   *
   * @param accountId linked account identifier
   * @param active target activation status
   * @return the updated former-student aggregate
   */
  FormerStudent updateStatus(UUID accountId, boolean active);
}
