package br.org.catolicasc.pug.project.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Attendance} aggregate roots.
 *
 * <p>This interface defines the contract for tracking formerStudent participation in projects,
 * including QR code validations and temporal checks.
 */
public interface AttendanceRepository {

  /**
   * Removes all attendance records associated with a specific project/formerStudent pair.
   *
   * @param projectId the unique identifier of the project
   * @param formerStudentId the unique identifier of the formerStudent account
   * @return the total number of attendance records successfully deleted
   */
  long deleteAllByEnrollmentId(UUID projectId, UUID formerStudentId);

  /**
   * Removes an {@link Attendance} record from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the attendance
   * @return {@code true} if deleted, {@code false} if not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether an attendance record has already been validated using the given QR hash.
   *
   * @param qrHash the cryptographic hash of the QR code
   * @return {@code true} if an attendance with this hash exists, {@code false} otherwise
   */
  boolean existsByQrHash(String qrHash);

  /**
   * Checks whether an attendance record has been validated by a specific account.
   *
   * @param accountId the unique identifier of the account
   * @return {@code true} if any record exists, {@code false} otherwise
   */
  boolean existsByValidatedBy(UUID accountId);

  /**
   * Retrieves an {@link Attendance} aggregate by its unique identifier.
   *
   * @param id the unique identifier of the attendance record
   * @return an {@link Optional} containing the {@link Attendance} if found, or {@link
   *     Optional#empty()} if not
   */
  Optional<Attendance> findOptionalById(UUID id);

  /**
   * Retrieves an {@link Attendance} by its unique QR code hash.
   *
   * @param qrHash the unique cryptographic hash
   * @return an {@link Optional} containing the Attendance if found
   */
  Optional<Attendance> findOptionalByQrHash(String qrHash);

  /**
   * Persists a newly created {@link Attendance} aggregate into the repository.
   *
   * @param entity the {@link Attendance} aggregate to persist
   * @return the fully persisted {@link Attendance} instance
   */
  Attendance persist(Attendance entity);

  /**
   * Updates the state of an existing {@link Attendance} aggregate in the repository.
   *
   * @param entity the {@link Attendance} instance containing the updated state
   */
  void update(Attendance entity);
}
