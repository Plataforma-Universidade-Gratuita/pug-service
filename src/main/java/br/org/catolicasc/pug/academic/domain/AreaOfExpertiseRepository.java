/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link AreaOfExpertise} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * academic areasOfExpertise (or departments). It abstracts the underlying data storage mechanism to
 * maintain a pure, infrastructure-agnostic domain model.
 */
public interface AreaOfExpertiseRepository {

  /**
   * Removes a {@link AreaOfExpertise} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the areaOfExpertise to delete
   * @return {@code true} if the areaOfExpertise was successfully deleted, {@code false} if it was
   *     not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether a {@link AreaOfExpertise} with the specified name already exists in the
   * repository.
   *
   * @param name the exact name of the areaOfExpertise
   * @return {@code true} if a areaOfExpertise with the given name exists, {@code false} otherwise
   */
  boolean existsByName(String name);

  /**
   * Retrieves a {@link AreaOfExpertise} by its unique identifier.
   *
   * <p>When a areaOfExpertise is reconstituted from the persistence layer, it typically undergoes
   * the same domain validations as a newly created aggregate. Therefore, the returned {@link
   * AreaOfExpertise} might contain validation errors (verifiable via {@link
   * AreaOfExpertise#hasFieldErrors()}) if the stored data violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the areaOfExpertise
   * @return an {@link Optional} containing the found {@link AreaOfExpertise}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<AreaOfExpertise> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link AreaOfExpertise} aggregate into the repository.
   *
   * @param entity the {@link AreaOfExpertise} aggregate to persist
   * @return the fully persisted {@link AreaOfExpertise} instance
   */
  AreaOfExpertise persist(AreaOfExpertise entity);

  /**
   * Updates the state of an existing {@link AreaOfExpertise} aggregate in the repository.
   *
   * @param entity the {@link AreaOfExpertise} instance containing the updated state
   */
  void update(AreaOfExpertise entity);
}
