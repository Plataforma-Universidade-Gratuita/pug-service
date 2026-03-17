package com.pug.project.service;

import com.pug.project.infra.read.dtos.ProjectView;
import java.util.List;
import java.util.UUID;

/** Application service interface dedicated exclusively to querying Project data. */
public interface ProjectReadService {

  /**
   * Retrieves a read-only projection of a project based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the populated {@link ProjectView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found
   */
  ProjectView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all projects registered in the system.
   *
   * @return a {@link List} containing all available {@link ProjectView} entries
   */
  List<ProjectView> listViews();

  /**
   * Retrieves a list of projects offered by a specific partner entity.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return a {@link List} of matching {@link ProjectView} entries
   */
  List<ProjectView> listViewsByEntityId(UUID entityId);

  /**
   * Executes a robust full-text search against the names of projects.
   *
   * @param query the raw search string
   * @return a sorted {@link List} of matching {@link ProjectView} entries
   */
  List<ProjectView> searchByName(String query);
}
