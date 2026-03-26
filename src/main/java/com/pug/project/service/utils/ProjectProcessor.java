package com.pug.project.service.utils;

import com.pug.project.domain.Project;
import com.pug.project.domain.ProjectsBySchool;
import com.pug.project.domain.vos.ProjectInfo;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Project}
 * and {@link ProjectsBySchool} Domain Aggregates.
 */
public final class ProjectProcessor {

  /** Private constructor to prevent instantiation of utility class. */
  private ProjectProcessor() {}

  /** Processes raw inputs and constructs a new {@link Project} domain aggregate. */
  public static Project processCreateInput(
      String name,
      UUID entityId,
      String description,
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours) {
    return Project.factory(name, entityId, description, createdBy, maxParticipants, offeredHours);
  }

  /**
   * Processes raw inputs and constructs a new {@link ProjectsBySchool} domain aggregate.
   *
   * @param projectId the unique identifier of the project
   * @param schoolId the unique identifier of the school
   * @return a fully instantiated {@link ProjectsBySchool} domain aggregate
   */
  public static ProjectsBySchool processCreateProjectBySchoolInput(UUID projectId, UUID schoolId) {
    return ProjectsBySchool.factory(projectId, schoolId);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Project}.
   */
  public static Project processUpdateInput(
      Project existingProject,
      String name,
      String description,
      Integer maxParticipants,
      BigDecimal offeredHours) {

    Project updated = existingProject;

    if (StringUtils.isNotEmpty(name)) {
      updated = updated.rename(name);
    }
    if (description != null) {
      updated = updated.changeDescription(description);
    }
    if (maxParticipants != null) {
      ProjectInfo newInfo = updated.getProjectInfo().changeMaxParticipantsAllowed(maxParticipants);
      updated = updated.toBuilder().projectInfo(newInfo).build();
    }
    if (offeredHours != null) {
      ProjectInfo newInfo = updated.getProjectInfo().changeOfferedHours(offeredHours);
      updated = updated.toBuilder().projectInfo(newInfo).build();
    }

    return updated;
  }
}
