/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link FormerStudent} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * formerStudent enrollments. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface FormerStudentRepository {

  /**
   * Removes a {@link FormerStudent} from the repository based on their linked account ID.
   *
   * @param id the unique identifier (UUID) of the formerStudent's linked account
   * @return {@code true} if the formerStudent was successfully deleted, {@code false} if not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any of the specified academic registrations already exist in the repository.
   *
   * @param registrations a {@link List} of academic registration strings to check
   * @return {@code true} if at least one registration exists, {@code false} otherwise
   */
  boolean existsAnyByRegistrations(List<String> registrations);

  /**
   * Checks whether any {@link FormerStudent} associated with the specified course identifier exists
   * in the repository.
   *
   * <p>This query enforces relational integrity, ensuring academic courses are not deleted if they
   * still have former students actively enrolled in them.
   *
   * @param courseId the unique identifier (UUID) of the enrolled course
   * @return {@code true} if at least one formerStudent is enrolled in the course, {@code false}
   *     otherwise
   */
  boolean existsByCourseId(UUID courseId);

  /**
   * Checks whether a {@link FormerStudent} with the specified academic registration already exists.
   *
   * @param registration the raw academic registration string to check
   * @return {@code true} if a formerStudent with the given registration exists, {@code false}
   *     otherwise
   */
  boolean existsByRegistration(String registration);

  /**
   * Resolves the academic area of expertise linked to the specified former student.
   *
   * <p>This query is intended for write-side orchestration where only the student's resolved area
   * linkage is needed, without reconstituting the full course aggregate.
   *
   * @param id the unique identifier (UUID) of the former student's linked account
   * @return the linked {@link AreaOfExpertise}, or {@code null} if no linkage exists
   */
  AreaOfExpertise findAreaOfExpertise(UUID id);

  /**
   * Retrieves a {@link FormerStudent} by their linked account identifier.
   *
   * <p>When a formerStudent is reconstituted from the persistence layer, it might contain
   * validation errors (verifiable via {@link FormerStudent#hasFieldErrors()}) if the stored data is
   * inconsistent with current domain rules.
   *
   * @param id the unique identifier (UUID) of the formerStudent's linked account
   * @return an {@link Optional} containing the found {@link FormerStudent}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<FormerStudent> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link FormerStudent} aggregate into the repository.
   *
   * @param entity the {@link FormerStudent} aggregate to persist
   * @return the fully persisted {@link FormerStudent} instance
   */
  FormerStudent persist(FormerStudent entity);

  /**
   * Persists a collection of newly created {@link FormerStudent} aggregates in a single batch.
   *
   * @param formerStudents a {@link List} of {@link FormerStudent} aggregates to persist
   * @return the fully persisted {@link List} of {@link FormerStudent} instances
   */
  List<FormerStudent> persistAll(List<FormerStudent> formerStudents);

  /**
   * Updates the state of an existing {@link FormerStudent} aggregate in the repository.
   *
   * @param entity the {@link FormerStudent} instance containing the updated state
   */
  void update(FormerStudent entity);
}
