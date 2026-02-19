package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing project information. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class ProjectInfo extends DomainError {

  UUID createdBy;
  OffsetDateTime createAt;
  OffsetDateTime closedAt;
  Integer maxParticipants;

  /** Private constructor for ProjectInfo. */
  @Builder(toBuilder = true)
  private ProjectInfo(
      UUID createdBy, OffsetDateTime createAt, OffsetDateTime closedAt, Integer maxParticipants) {
    this.createdBy = createdBy;
    this.createAt = createAt;
    this.closedAt = closedAt;
    this.maxParticipants = maxParticipants;
  }

  /**
   * Factory method to create a ProjectInfo instance with validation.
   *
   * @param createdBy the UUID of the creator
   * @param createAt the creation timestamp
   * @param closedAt the closing timestamp
   * @param maxParticipants the maximum number of participants
   * @return a validated ProjectInfo instance
   */
  public static ProjectInfo factory(
      UUID createdBy, OffsetDateTime createAt, OffsetDateTime closedAt, Integer maxParticipants) {

    ProjectInfo vo =
        ProjectInfo.builder()
            .createdBy(createdBy)
            .createAt(createAt)
            .closedAt(closedAt)
            .maxParticipants(maxParticipants)
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the ProjectInfo fields and adds errors if any validation fails. */
  private void collectValidationProblems() {
    if (createdBy == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_BY_BLANK));
    }

    if (createAt == null) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_AT_BLANK));
    }

    if (maxParticipants != null && maxParticipants < 0) {
      addError(
          new AppValidationException.Problem(ProjectsErrorCodes.INVALID_MAX_PARTICIPANTS_NEGATIVE));
    }

    if (createAt != null && closedAt != null && closedAt.isBefore(createAt)) {
      addError(new AppValidationException.Problem(ProjectsErrorCodes.INVALID_CREATED_AT_FUTURE));
    }
  }
}
