package com.pug.project.infra.read;

import com.pug.project.infra.read.dtos.SchoolProjectView;
import java.util.List;
import java.util.UUID;

/** Read-only interface for executing queries against Project-to-School associations. */
public interface ProjectsBySchoolsQueries {

  /**
   * Retrieves a consolidated view of a school and its associated projects.
   *
   * @param schoolId the unique identifier (UUID) of the school
   * @return the populated {@link SchoolProjectView} DTO
   */
  SchoolProjectView listBySchool(UUID schoolId);

  /**
   * Retrieves all school identifiers associated with a specific project.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return a {@link List} of school UUIDs
   */
  List<UUID> listAllSchoolsIdsByProjectId(UUID projectId);
}
