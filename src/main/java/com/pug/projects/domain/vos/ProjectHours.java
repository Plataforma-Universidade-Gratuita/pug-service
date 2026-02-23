package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing project hours. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class ProjectHours extends DomainError {

  BigDecimal offeredHours;
  BigDecimal completedHours;

  /** Private constructor for ProjectHours value object. */
  @Builder(toBuilder = true)
  private ProjectHours(BigDecimal offeredHours, BigDecimal completedHours) {
    this.offeredHours = offeredHours;
    this.completedHours = completedHours;
  }

  /**
   * Factory method to create a ProjectHours value object.
   *
   * @param offeredHours Hours offered for the project.
   * @param completedHours Hours completed for the project.
   * @return A validated ProjectHours value object.
   */
  public static ProjectHours factory(BigDecimal offeredHours, BigDecimal completedHours) {
    BigDecimal comp = completedHours != null ? completedHours : BigDecimal.ZERO;

    ProjectHours vo =
        ProjectHours.builder().offeredHours(offeredHours).completedHours(comp).build();
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the ProjectHours value object. */
  private void collectValidationProblems() {
    if (offeredHours == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_OFFERED_HOURS_NEGATIVE));
      return;
    }

    if (offeredHours.signum() < 0) {
      addError(new Problem(ProjectsErrorCodes.INVALID_OFFERED_HOURS_NEGATIVE));
    }

    if (completedHours.signum() < 0) {
      addError(new Problem(ProjectsErrorCodes.INVALID_COMPLETED_HOURS_NEGATIVE));
    }

    if (completedHours.compareTo(offeredHours) > 0) {
      addError(new Problem(ProjectsErrorCodes.INVALID_COMPLETED_HOURS_EXCEEDS));
    }
  }
}
