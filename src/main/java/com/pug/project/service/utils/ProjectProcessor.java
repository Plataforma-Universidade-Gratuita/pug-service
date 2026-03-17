package com.pug.project.service.utils;

import com.pug.project.domain.Project;
import com.pug.project.domain.vos.ProjectInfo;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Project}
 * Domain Aggregates.
 */
public class ProjectProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Project} domain aggregate.
   *
   * @param name the raw name of the project
   * @param entityId the unique identifier of the partner organization
   * @param description the project description
   * @param createdBy the unique identifier of the staff member
   * @param maxParticipants the maximum number of participants
   * @param offeredHours the total hours offered
   * @return a fully instantiated {@link Project} domain aggregate, potentially containing errors
   */
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
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Project}.
   *
   * @param existingProject the current, reconstituted {@link Project} aggregate
   * @param name the proposed new name, or {@code null}
   * @param description the proposed new description, or {@code null}
   * @param maxParticipants the proposed new maximum participants limit, or {@code null}
   * @param offeredHours the proposed new offered hours, or {@code null}
   * @return a new {@link Project} domain aggregate reflecting the requested updates
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
    if (StringUtils.isNotEmpty(description)) {
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
