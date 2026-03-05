package com.pug.projects.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Project} aggregate roots.
 * <p>
 * This interface defines the contract for persisting, retrieving, updating, and deleting
 * projects. It abstracts the underlying data storage mechanism to maintain
 * a pure, infrastructure-agnostic domain model.
 */
public interface ProjectRepository {

  Project persist(Project entity);

  void update(Project entity);

  boolean deleteById(UUID id);

  Optional<Project> findOptionalById(UUID id);

  /**
   * Checks whether a project with the specified name already exists for a given partner entity.
   *
   * @param name     the exact name of the project
   * @param entityId the unique identifier of the partner organization
   * @return {@code true} if a matching project exists, {@code false} otherwise
   */
  boolean existsByNameAndEntityId(String name, UUID entityId);
}