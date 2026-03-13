package com.pug.project.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.project.domain.enums.ProjectStatus;
import com.pug.project.domain.enums.ProjectsErrorCodes;
import com.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.pug.project.domain.vos.ProjectInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable Domain Entity representing a Project offered by a Partner Entity.
 *
 * <p>This class acts as an aggregate root containing the project's unique identifier, descriptive
 * data, physical limitations (max participants, hours offered), and lifecycle state. It extends
 * {@link DomainError} to accumulate structural validation failures.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Project extends DomainError {

  /** The unique identifier for the project (UUIDv7). */
  UUID id;

  /** The title or name of the project. */
  String name;

  /** The unique identifier of the partner organization offering this project. */
  UUID entityId;

  /** The detailed description of the project's objectives and tasks. */
  String description;

  /** The logistical metadata and audit information of the project. */
  ProjectInfo projectInfo;

  /** The current execution state of the project (e.g., PLANNED, IN_PROGRESS). */
  ProjectStatus projectStatus;

  /**
   * Factory method to create a new {@code Project} instance.
   *
   * <p>The project is initialized in a {@code PLANNED} state with standard tracking information.
   *
   * @param name the name of the project
   * @param entityId the associated partner entity
   * @param description the project description
   * @param createdBy the UUID of the staff account who created the project
   * @param maxParticipants the maximum number of participants allowed
   * @param offeredHours the total hours offered for completing the project
   * @return a newly created and self-validated {@link Project} instance
   */
  public static Project factory(
      String name,
      UUID entityId,
      String description,
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours) {
    ProjectInfo infoVo = ProjectInfo.factory(createdBy, maxParticipants, offeredHours);

    Project project =
        Project.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(StringUtils.trim(name))
            .entityId(entityId)
            .description(StringUtils.trim(description))
            .projectInfo(infoVo)
            .projectStatus(ProjectStatus.PLANNED)
            .build();

    project.collectValidationProblems();
    return project;
  }

  /**
   * Updates the project's title.
   *
   * @param newName the new name for the project
   * @return a new {@link Project} instance with the updated name
   */
  public Project rename(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (name.equals(trimmed)) {
      return this;
    }
    Project updated = toBuilder().name(trimmed).projectInfo(projectInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Reassigns the project to a different partner organization.
   *
   * @param newEntityId the unique identifier of the new partner entity
   * @return a new {@link Project} instance with the updated entity ID
   */
  public Project moveToEntity(UUID newEntityId) {
    if (entityId.equals(newEntityId)) {
      return this;
    }
    Project updated = toBuilder().entityId(newEntityId).projectInfo(projectInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Updates the project's description.
   *
   * @param newDescription the new description text
   * @return a new {@link Project} instance with the updated description
   */
  public Project changeDescription(String newDescription) {
    String trimmed = StringUtils.trim(newDescription);
    if (description.equals(trimmed)) {
      return this;
    }
    Project updated = toBuilder().description(trimmed).projectInfo(projectInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Transitions the project's state from 'PLANNED' to 'IN_PROGRESS'.
   *
   * @return a new {@link Project} instance reflecting the started status
   * @throws BusinessRuleException if the project is not currently in a 'PLANNED' state
   */
  public Project start() {
    if (projectStatus == ProjectStatus.IN_PROGRESS) {
      return this;
    }
    if (projectStatus != ProjectStatus.PLANNED) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_START);
    }
    Project updated =
        toBuilder()
            .projectStatus(ProjectStatus.IN_PROGRESS)
            .projectInfo(projectInfo.update())
            .build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Transitions the project's state from 'IN_PROGRESS' to 'COMPLETED'.
   *
   * @return a new {@link Project} instance reflecting the completed status
   * @throws BusinessRuleException if the project is not currently 'IN_PROGRESS'
   */
  public Project complete() {
    if (projectStatus == ProjectStatus.COMPLETED) {
      return this;
    }
    if (projectStatus != ProjectStatus.IN_PROGRESS) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_COMPLETE);
    }
    Project updated =
        toBuilder()
            .projectStatus(ProjectStatus.COMPLETED)
            .projectInfo(projectInfo.closeProject())
            .build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Transitions the project's state to 'CANCELED'.
   *
   * @return a new {@link Project} instance reflecting the canceled status
   * @throws BusinessRuleException if the project is already 'COMPLETED'
   */
  public Project cancel() {
    if (projectStatus == ProjectStatus.CANCELED) {
      return this;
    }
    if (projectStatus == ProjectStatus.COMPLETED) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_CANCEL);
    }
    Project updated =
        toBuilder()
            .projectStatus(ProjectStatus.CANCELED)
            .projectInfo(projectInfo.closeProject())
            .build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Transitions the project's state from 'IN_PROGRESS' to 'ON_HOLD'.
   *
   * @return a new {@link Project} instance reflecting the on-hold status
   * @throws BusinessRuleException if the project is not currently 'IN_PROGRESS'
   */
  public Project putOnHold() {
    if (projectStatus == ProjectStatus.ON_HOLD) {
      return this;
    }
    if (projectStatus != ProjectStatus.IN_PROGRESS) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_PUT_ON_HOLD);
    }
    Project updated =
        toBuilder().projectStatus(ProjectStatus.ON_HOLD).projectInfo(projectInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Transitions the project's state from 'ON_HOLD' back to 'IN_PROGRESS'.
   *
   * @return a new {@link Project} instance reflecting the resumed status
   * @throws BusinessRuleException if the project is not currently 'ON_HOLD'
   */
  public Project retake() {
    if (projectStatus != ProjectStatus.ON_HOLD) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_RETAKE);
    }
    Project updated =
        toBuilder()
            .projectStatus(ProjectStatus.IN_PROGRESS)
            .projectInfo(projectInfo.update())
            .build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Evaluates constraints for the Project aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    validateIdField(id);
    if (entityId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_CREATED_BY_BLANK);
    }
    if (StringUtils.isEmpty(description)) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_DESCRIPTION_BLANK);
    } else if (description.length() > 4000) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_DESCRIPTION_TOO_LONG);
    }
    if (StringUtils.isEmpty(name)) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_NAME_BLANK);
    } else if (name.length() > 150) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_NAME_TOO_LONG);
    }
    if (projectInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (projectInfo.hasFieldErrors()) {
      addFieldErrors(projectInfo.getFieldErrors());
    }
    if (projectStatus == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_STATUS_BLANK);
    }
  }
}
