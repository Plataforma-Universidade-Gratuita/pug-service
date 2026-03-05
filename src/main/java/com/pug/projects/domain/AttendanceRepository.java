package com.pug.projects.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Attendance} aggregate roots.
 *
 * <p>This interface defines the contract for tracking student participation in projects, including
 * QR code validations and temporal geographic checks.
 */
public interface AttendanceRepository {

  Attendance persist(Attendance entity);

  void update(Attendance entity);

  boolean deleteById(UUID id);

  Optional<Attendance> findOptionalById(UUID id);

  /**
   * Checks whether an attendance record has already been validated using the given QR hash.
   *
   * @param qrHash the cryptographic hash of the QR code
   * @return {@code true} if an attendance with this hash exists, {@code false} otherwise
   */
  boolean existsByQrHash(String qrHash);

  boolean existsByValidatedBy(UUID staffAccountId);
}
