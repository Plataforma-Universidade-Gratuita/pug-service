package com.pug.projects.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.academic.domain.School;
import com.pug.partner.domain.Entity;
import com.pug.projects.domain.enums.ProjectStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.ProjectHours;
import com.pug.projects.domain.vos.ProjectInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import com.pug.shared.utils.StringUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity representing a Project.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Project extends DomainError {
  private final UUID id;
  private final String name;
  private final Entity entity;
  private final String description;
  private final ProjectHours projectHours;
  private final ProjectInfo projectInfo;
  private final ProjectStatus projectStatus;
  private final List<School> schools;

  /**
   * Factory method to create a new Project instance.
   *
   * @param name          the name of the project
   * @param entity        the associated entity
   * @param description   the project description
   * @param createdBy     the UUID of the user who created the project
   * @param offeredHours  the number of hours offered for the project
   * @param maxParticipants the maximum number of participants allowed
   * @param schools       the list of associated schools
   * @param time          the TimeProvider for current time
   * @return a validated Project instance
   */
  public static Project factory(
          String name,
          Entity entity,
          String description,
          UUID createdBy,
          BigDecimal offeredHours,
          Integer maxParticipants,
          List<School> schools,
          TimeProvider time) {

    var now = time.now();
    OffsetDateTime createdAt = OffsetDateTime.ofInstant(now, time.clock().getZone());

    ProjectHours hoursVo = ProjectHours.factory(offeredHours, BigDecimal.ZERO);
    ProjectInfo infoVo = ProjectInfo.factory(createdBy, createdAt, null, maxParticipants);

    Project project = Project.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(StringUtils.trim(name))
            .entity(entity)
            .description(StringUtils.trim(description))
            .projectHours(hoursVo)
            .projectInfo(infoVo)
            .projectStatus(ProjectStatus.PLANNED)
            .schools(schools != null ? new ArrayList<>(schools) : new ArrayList<>())
            .build();

    project.collectValidationProblems();
    return project;
  }

  public Project updateDetails(
          String name,
          String description,
          BigDecimal offeredHours,
          Integer maxParticipants,
          List<School> schools) {

    Project.ProjectBuilder builder = this.toBuilder();

    if (!StringUtils.isEmpty(name)) {
      builder.name(StringUtils.trim(name));
    }

    if (!StringUtils.isEmpty(description)) {
      builder.description(StringUtils.trim(description));
    }

    if (offeredHours != null) {
      ProjectHours newHours = ProjectHours.factory(offeredHours, this.projectHours.getCompletedHours());
      builder.projectHours(newHours);
    }

    if (maxParticipants != null) {
      ProjectInfo newInfo = ProjectInfo.factory(
              this.projectInfo.getCreatedBy(),
              this.projectInfo.getCreateAt(),
              this.projectInfo.getClosedAt(),
              maxParticipants);
      builder.projectInfo(newInfo);
    }

    if (schools != null) {
      builder.schools(new ArrayList<>(schools));
    }

    Project updated = builder.build();
    updated.collectValidationProblems();
    return updated;
  }

  public Project changeStatus(ProjectStatus newStatus, TimeProvider time) {
    if (this.projectStatus == newStatus) return this;

    OffsetDateTime closedAt = this.projectInfo.getClosedAt();
    if (newStatus == ProjectStatus.COMPLETED || newStatus == ProjectStatus.CANCELLED) {
      closedAt = OffsetDateTime.now(time.clock());
    } else if (newStatus == ProjectStatus.IN_PROGRESS || newStatus == ProjectStatus.PLANNED) {
      closedAt = null;
    }

    ProjectInfo newInfo = ProjectInfo.factory(
            this.projectInfo.getCreatedBy(),
            this.projectInfo.getCreateAt(),
            closedAt,
            this.projectInfo.getMaxParticipants());

    Project updated = this.toBuilder()
            .projectStatus(newStatus)
            .projectInfo(newInfo)
            .build();

    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    if (id == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_PROJECT_ID_BLANK));
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_PROJECT_NAME_BLANK));
    } else if (name.length() > 150) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_PROJECT_NAME_LENGTH));
    }

    if (entity == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENTITY_ID_BLANK));
    }

    if (StringUtils.isEmpty(description)) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_DESCRIPTION_BLANK));
    } else if (description.length() > 4000) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_DESCRIPTION_LENGTH));
    }

    if (projectInfo == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_AT_BLANK));
    } else if (projectInfo.hasErrors()) {
      addErrors(projectInfo.getProblems());
    }

    if (projectHours == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_OFFERED_HOURS_NEGATIVE));
    } else if (projectHours.hasErrors()) {
      addErrors(projectHours.getProblems());
    }

    if (projectStatus == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_STATUS_BLANK));
    }
  }
}