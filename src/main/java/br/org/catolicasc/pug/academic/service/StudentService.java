package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Student} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete). It orchestrates the complex lifecycle relationship between a Student enrollment, their
 * underlying {@link Account}, and the {@link
 * Course} they are enrolled in, ensuring that academic records and
 * authentication credentials cascade correctly.
 */
public interface StudentService {

  /**
   * Adds completed hours to a student's academic progress.
   *
   * <p>This method updates the student's record, recalculating whether the student has reached the
   * total required counterpart hours.
   *
   * @param accountId the student's account identifier
   * @param hours the amount of hours to add
   * @return the updated {@link Student} aggregate
   * @throws ResourceNotFoundException if the student does not exist
   */
  Student addCompletedHours(UUID accountId, BigDecimal hours);

  /**
   * Removes a student's enrollment by deleting the {@link Student} record.
   *
   * <p>This operation enforces data hygiene. After the academic enrollment is successfully removed,
   * the service automatically triggers the deletion of the underlying {@link
   * Account} to ensure credentials tied strictly to student roles are wiped
   * out.
   *
   * @param accountId the unique identifier of the Student to delete (Account ID)
   * @return {@code true} if the student was successfully deleted, {@code false} if they were not
   *     found
   */
  boolean delete(UUID accountId);

  /**
   * Checks whether any active student enrollment associated with a specific course exists.
   *
   * <p>This method is utilized to enforce relational integrity, ensuring that an academic {@link
   * Course} cannot be deleted if it still has enrolled students.
   *
   * @param courseId the unique identifier (UUID) of the course to check
   * @return {@code true} if at least one student is enrolled in the course, {@code false} otherwise
   */
  boolean existsAnyByCourseId(UUID courseId);

  /**
   * Retrieves a full {@link Student} domain aggregate by its linked account identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link StudentReadService#getViewByAccountId(UUID)} instead.
   *
   * @param accountId the unique identifier (UUID) of the linked account
   * @return the fully reconstituted {@link Student} aggregate
   * @throws ResourceNotFoundException if the student does not exist
   * @throws AppValidationException if the student exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  Student getById(UUID accountId);

  /**
   * Instantiates and persists a new {@link Student} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It verifies that the specified course exists, then
   * delegates the creation of the underlying authentication account (and potentially the user
   * identity) to the {@link AccountService} before saving the student's
   * academic enrollment records.
   *
   * @param cmd the structured command containing the data to create the student and linked account
   * @return the fully instantiated and persisted {@link Student} aggregate
   * @throws DuplicateResourceException if the academic registration or
   *     account email already exists
   * @throws ResourceNotFoundException if the associated course does not
   *     exist
   * @throws AppValidationException if input validation fails at the
   *     domain level
   */
  Student save(StudentCreateCommand cmd);

  /**
   * Instantiates and persists multiple {@link Student} aggregates in a single batch transaction.
   *
   * <p>This method iterates over the provided commands, applying the exact same domain rules,
   * validations, and cascading identity provisions as a single creation, ensuring that the entire
   * batch operation succeeds or fails together.
   *
   * @param cmds a {@link List} of structured commands containing the data to create the students
   * @return a {@link List} of the fully instantiated and persisted {@link Student} aggregates
   * @throws DuplicateResourceException if any academic registration or
   *     email already exists
   * @throws ResourceNotFoundException if any associated course does not
   *     exist
   * @throws AppValidationException if input validation fails for any
   *     record
   */
  List<Student> saveInBulk(List<StudentCreateCommand> cmds);

  /**
   * Updates an existing {@link Student} and optionally its underlying account using the provided
   * data.
   *
   * <p>This method applies partial updates. If account data is provided in the command, the update
   * is cascaded down to the underlying account aggregate. If a new course ID is provided, the
   * service verifies its existence before changing the student's enrollment.
   *
   * @param accountId the unique identifier of the Student (which corresponds directly to the
   *     Account ID)
   * @param cmd the structured command containing the new data for the student and/or account
   * @return the mutated and persisted {@link Student} aggregate
   * @throws ResourceNotFoundException if the student does not exist, or
   *     the new course is not found
   * @throws DuplicateResourceException if the updated academic
   *     registration or email conflicts with existing records
   * @throws AppValidationException if input validation fails
   */
  Student update(UUID accountId, StudentUpdateCommand cmd);
}
