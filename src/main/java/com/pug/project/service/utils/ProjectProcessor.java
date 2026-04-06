package com.pug.project.service.utils;

import com.pug.project.domain.Project;
import com.pug.project.domain.vos.ProjectInfo;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Project}
 * Domain Aggregates.
 *
 * <p>This processor centralizes orchestration of {@link Project} factory methods and state‑mutation
 * behaviors, ensuring that application services remain focused on coordination, cross‑aggregate
 * rules, and error handling rather than low-level aggregate construction details.
 */
public final class ProjectProcessor {

  /** Private constructor to prevent instantiation of utility class. */
  private ProjectProcessor() {}

  /**
   * Processes raw creation inputs and constructs a new {@link Project} domain aggregate.
   *
   * <p>This method delegates to {@link Project#factory(String, UUID, String, UUID, Integer,
   * BigDecimal)}, which immediately self-validates the created aggregate. The caller is responsible
   * for inspecting {@link Project#hasFieldErrors()} and reacting appropriately (for example, by
   * throwing an {@link com.pug.shared.exceptions.AppValidationException}) when validation problems
   * are present.
   *
   * @param name the raw project name
   * @param entityId the unique identifier of the partner entity offering the project
   * @param description the optional detailed project description
   * @param createdBy the unique identifier of the staff account that is creating the project
   * @param maxParticipants the maximum number of students allowed to enroll (may be {@code null})
   * @param offeredHours the total counterpart hours offered by the project
   * @return a fully instantiated {@link Project} domain aggregate, potentially containing
   *     validation errors
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
   * <p>This method applies partial updates to the provided {@code existingProject}. Only fields
   * that are explicitly present in the input (non-{@code null} and, for strings, non-empty) will
   * trigger a state mutation via the aggregate's domain behaviors:
   *
   * <ul>
   *   <li>{@code name} &rarr; {@link Project#rename(String)}
   *   <li>{@code description} &rarr; {@link Project#changeDescription(String)}
   *   <li>{@code maxParticipants} &rarr; {@link ProjectInfo#changeMaxParticipantsAllowed(Integer)}
   *   <li>{@code offeredHours} &rarr; {@link ProjectInfo#changeOfferedHours(BigDecimal)}
   * </ul>
   *
   * <p>Because {@link Project} is modeled as an immutable aggregate, this method returns a
   * <em>new</em> {@link Project} instance reflecting the requested updates. The caller is
   * responsible for checking {@link Project#hasFieldErrors()} on the returned instance.
   *
   * @param existingProject the current, reconstituted {@link Project} aggregate from the repository
   * @param name the proposed new name for the project, or {@code null} to leave unchanged
   * @param description the proposed new description, or {@code null} to leave unchanged
   * @param maxParticipants the proposed new maximum number of participants, or {@code null} to
   *     leave unchanged
   * @param offeredHours the proposed new offered hours, or {@code null} to leave unchanged
   * @return a new {@link Project} aggregate reflecting the requested updates, potentially
   *     containing validation errors
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
