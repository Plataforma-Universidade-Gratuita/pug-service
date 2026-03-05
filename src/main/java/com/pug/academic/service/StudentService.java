package com.pug.academic.service;

import com.pug.academic.domain.Student;
import com.pug.academic.service.dtos.StudentCreateCommand;
import com.pug.academic.service.dtos.StudentUpdateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Student} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete). It orchestrates the complex lifecycle relationship between a Student enrollment, their
 * underlying {@link com.pug.identity.domain.Account}, and the {@link
 * com.pug.academic.domain.Course} they are enrolled in, ensuring that academic records and
 * authentication credentials cascade correctly.
 */
public interface StudentService {

  /**
   * Instantiates and persists a new {@link Student} aggregate based on the provided command.
   *
   * <p>This method performs a cascading save. It verifies that the specified course exists, then
   * delegates the creation of the underlying authentication account (and potentially the user
   * identity) to the {@link com.pug.identity.service.AccountService} before saving the student's
   * academic enrollment records.
   *
   * @param cmd the structured command containing the data to create the student and linked account
   * @return the fully instantiated and persisted {@link Student} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the academic registration or
   *     account email already exists
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the associated course does not
   *     exist
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails at the
   *     domain level
   */
  Student save(StudentCreateCommand cmd);

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
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student does not exist, or
   *     the new course is not found
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the updated academic
   *     registration or email conflicts with existing records
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails
   */
  Student update(UUID accountId, StudentUpdateCommand cmd);

  /**
   * Removes a student's enrollment by deleting the {@link Student} record.
   *
   * <p>This operation enforces data hygiene. After the academic enrollment is successfully removed,
   * the service automatically triggers the deletion of the underlying {@link
   * com.pug.identity.domain.Account} to ensure credentials tied strictly to student roles are wiped
   * out.
   *
   * @param accountId the unique identifier of the Student to delete (Account ID)
   * @return {@code true} if the student was successfully deleted, {@code false} if they were not
   *     found
   */
  boolean delete(UUID accountId);

  /**
   * Retrieves a full {@link Student} domain aggregate by its linked account identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link StudentReadService#getViewByAccountId(UUID)} instead.
   *
   * @param accountId the unique identifier (UUID) of the linked account
   * @return the fully reconstituted {@link Student} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the student does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the student exists but its stored
   *     state violates strict domain invariants (data corruption)
   */
  Student getById(UUID accountId);
}
