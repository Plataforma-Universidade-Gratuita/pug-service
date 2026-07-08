package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.vos.ProjectInfo;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Project}
 * Domain Aggregates.
 */
public final class ProjectProcessor {

  private ProjectProcessor() {}

  /**
   * Processes raw creation inputs and constructs a new {@link Project} domain aggregate.
   *
   * <p>New projects are initialized with {@code completedHours} set to zero.
   *
   * @param name the raw project name
   * @param entityId the unique identifier of the partner entity
   * @param description the optional detailed project description
   * @param createdBy the unique identifier of the staff account
   * @param maxParticipants the maximum number of formerStudents allowed
   * @param offeredHours the total counterpart hours offered
   * @return a fully instantiated {@link Project} domain aggregate
   */
  public static Project processCreateInput(
      String name,
      UUID entityId,
      String description,
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours) {

    return Project.factory(
        name, entityId, description, createdBy, maxParticipants, offeredHours, BigDecimal.ZERO);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Project}.
   *
   * <p>Applies partial updates to name, description, participants, and offered hours.
   *
   * @param existingProject the current, reconstituted {@link Project} aggregate
   * @param name the proposed new name, or {@code null} to leave unchanged
   * @param description the proposed new description, or {@code null} to leave unchanged
   * @param maxParticipants the proposed new maximum participants, or {@code null} to leave
   *     unchanged
   * @param offeredHours the proposed new offered hours, or {@code null} to leave unchanged
   * @return a new {@link Project} aggregate reflecting the requested updates
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
