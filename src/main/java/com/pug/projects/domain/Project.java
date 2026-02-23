package com.pug.projects.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.projects.domain.enums.ProjectStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.ProjectInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Domain entityId representing a Project. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Project extends DomainError {
  UUID id;
  String name;
  UUID entityId;
  String description;
  ProjectInfo projectInfo;
  ProjectStatus projectStatus;

  /**
   * Factory method to create a new Project instance.
   *
   * @param name the name of the project
   * @param entityId the associated entityId
   * @param description the project description
   * @param createdBy the UUID of the account who created the project
   * @param maxParticipants the maximum number of participants allowed
   * @param offeredHours the number of hours offered for the project
   * @return a validated Project instance
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
   * Behavior: rename the project.
   *
   * @param newName the new name for the project
   * @return a new Project instance with the updated name
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
   * Behavior: move the project to a different entityId.
   *
   * @param newEntityId the new entityId to associate with the project
   * @return a new Project instance with the updated entityId
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
   * Behavior: change the project description.
   *
   * @param newDescription the new description for the project
   * @return a new Project instance with the updated description
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
   * Behavior: start the project by changing its status to 'IN_PROGRESS'. If the project is already
   * in progress, it returns the same instance. If the project is not in a valid state to be
   * started, it throws a BusinessRuleException.
   *
   * @return a new Project instance with status 'IN_PROGRESS', or the same instance if already in
   *     progress
   * @throws BusinessRuleException if the project cannot be started due to invalid status
   */
  public Project start() {
    if (projectStatus == ProjectStatus.IN_PROGRESS) {
      return this;
    }
    if (projectStatus != ProjectStatus.PLANNED) {
      throw new BusinessRuleException(
          ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_START,
          "projectStatus",
          projectStatus.name());
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
   * Behavior: complete the project by changing its status to 'COMPLETED'. If the project is already
   * completed, it returns the same instance. If the project is not in a valid state to be
   * completed, it throws a BusinessRuleException.
   *
   * @return a new Project instance with status 'COMPLETED', or the same instance if already
   *     completed
   * @throws BusinessRuleException if the project cannot be completed due to invalid status
   */
  public Project complete() {
    if (projectStatus == ProjectStatus.COMPLETED) {
      return this;
    }
    if (projectStatus != ProjectStatus.IN_PROGRESS) {
      throw new BusinessRuleException(
          ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_COMPLETE,
          "projectStatus",
          projectStatus.name());
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
   * Behavior: cancel the project by changing its status to 'CANCELED'. If the project is already
   * canceled, it returns the same instance. If the project is not in a valid state to be canceled,
   * it throws a BusinessRuleException.
   *
   * @return a new Project instance with status 'CANCELED', or the same instance if already canceled
   * @throws BusinessRuleException if the project cannot be canceled due to invalid status
   */
  public Project cancel() {
    if (projectStatus == ProjectStatus.CANCELED) {
      return this;
    }
    if (projectStatus == ProjectStatus.COMPLETED) {
      throw new BusinessRuleException(
          ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_CANCEL,
          "projectStatus",
          projectStatus.name());
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
   * Behavior: put the project on hold by changing its status to 'ON_HOLD'. If the project is
   * already on hold, it returns the same instance. If the project is not in a valid state to be put
   * on hold, it throws a BusinessRuleException.
   *
   * @return a new Project instance with status 'ON_HOLD', or the same instance if already on hold
   * @throws BusinessRuleException if the project cannot be put on hold due to invalid status
   */
  public Project putOnHold() {
    if (projectStatus == ProjectStatus.ON_HOLD) {
      return this;
    }
    if (projectStatus != ProjectStatus.IN_PROGRESS) {
      throw new BusinessRuleException(
          ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_PUT_ON_HOLD,
          "projectStatus",
          projectStatus.name());
    }
    Project updated =
        toBuilder().projectStatus(ProjectStatus.ON_HOLD).projectInfo(projectInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: retake the project by changing its status from 'ON_HOLD' back to 'IN_PROGRESS'. If
   * the project is not currently on hold, it throws a BusinessRuleException.
   *
   * @return a new Project instance with status 'IN_PROGRESS' if it was on hold
   * @throws BusinessRuleException if the project cannot be retaken because it is not on hold
   */
  public Project retake() {
    if (projectStatus != ProjectStatus.ON_HOLD) {
      throw new BusinessRuleException(
          ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_RETAKE,
          "projectStatus",
          projectStatus.name());
    }
    Project updated =
        toBuilder()
            .projectStatus(ProjectStatus.IN_PROGRESS)
            .projectInfo(projectInfo.update())
            .build();
    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    validateIdField(id);
    validateForeignKeyField(entityId, "entityId");
    validateStringField(name, 150L, "name");
    validateStringField(description, 4000L, "description");

    if (projectInfo == null) {
      addError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else if (projectInfo.hasErrors()) {
      addErrors(projectInfo.getProblems());
    }

    if (projectStatus == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_STATUS_BLANK));
    }
  }
}
