package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.project.domain.vos.ProjectInfo;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.util.List;
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
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Project extends DomainError {

  UUID id;

  String name;

  UUID entityId;

  String description;

  ProjectInfo projectInfo;

  ProjectStatus projectStatus;

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
   * Updates the project's description.
   *
   * @param newDescription the new description text
   * @return a new {@link Project} instance with the updated description
   */
  public Project changeDescription(String newDescription) {
    String trimmed = StringUtils.trim(newDescription);
    if (description != null && description.equals(trimmed)) {
      return this;
    }
    Project updated = toBuilder().description(trimmed).projectInfo(projectInfo.update()).build();
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
   * @param completedHours the hours already completed (usually 0 at creation)
   * @return a newly created and self-validated {@link Project} instance
   */
  public static Project factory(
      String name,
      UUID entityId,
      String description,
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours,
      BigDecimal completedHours) {
    ProjectInfo infoVo =
        ProjectInfo.factory(createdBy, maxParticipants, offeredHours, completedHours);

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
   * Adiciona horas completadas ao progresso total do projeto.
   *
   * <p>Se o total de horas completadas atingir ou superar as horas oferecidas, o projeto é
   * automaticamente marcado como 'COMPLETED'.
   *
   * @param hours o valor de horas a ser adicionado
   * @return uma nova instância de {@link Project} com o progresso e status atualizados
   */
  public Project addCompletedHours(BigDecimal hours) {
    BigDecimal newTotal = projectInfo.getCompletedHours().add(hours);

    ProjectInfo updatedInfo =
        ProjectInfo.factory(
            projectInfo.getCreatedBy(),
            projectInfo.getMaxParticipants(),
            projectInfo.getOfferedHours(),
            newTotal);

    ProjectStatus newStatus = projectStatus;
    ProjectInfo finalInfo = updatedInfo;

    if (newTotal.compareTo(projectInfo.getOfferedHours()) >= 0) {
      newStatus = ProjectStatus.COMPLETED;
      finalInfo = updatedInfo.closeProject();
    }

    Project updated = toBuilder().projectInfo(finalInfo).projectStatus(newStatus).build();

    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Removes completed hours from the project's total progress.
   *
   * <p>If the total completed hours drops below the offered hours, the project status is preserved
   * and only the progress counters are updated.
   *
   * @param hours the amount of hours to remove
   * @return a new instance of {@link Project} with updated progress
   */
  public Project removeCompletedHours(BigDecimal hours) {
    BigDecimal newTotal = projectInfo.getCompletedHours().subtract(hours);

    ProjectInfo updatedInfo =
        ProjectInfo.factory(
            projectInfo.getCreatedBy(),
            projectInfo.getMaxParticipants(),
            projectInfo.getOfferedHours(),
            newTotal);

    Project updated = toBuilder().projectInfo(updatedInfo).build();

    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Validates whether the specified attendance duration can be added without exceeding the
   * project's offered hours.
   *
   * @param hours the attendance duration to add
   * @throws BusinessRuleException if the resulting total would exceed the offered hours
   */
  public void validateCanAddCompletedHours(BigDecimal hours) {
    if (projectInfo == null || hours == null) {
      return;
    }

    BigDecimal nextTotal = projectInfo.getCompletedHours().add(hours);
    if (nextTotal.compareTo(projectInfo.getOfferedHours()) > 0) {
      throw new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_PROJECT_HOURS_EXCEED);
    }
  }

  /**
   * Validates whether the specified attendance duration can be removed without making the project's
   * completed hours negative.
   *
   * @param hours the attendance duration to remove
   * @throws BusinessRuleException if the resulting total would be negative
   */
  public void validateCanRemoveCompletedHours(BigDecimal hours) {
    if (projectInfo == null || hours == null) {
      return;
    }

    BigDecimal nextTotal = projectInfo.getCompletedHours().subtract(hours);
    if (nextTotal.compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_PROJECT_HOURS_NEGATIVE);
    }
  }

  /**
   * Validates whether the project can receive new enrollments.
   *
   * @throws BusinessRuleException if the project is canceled or completed
   */
  public void validateCanReceiveEnrollments() {
    if (projectStatus == ProjectStatus.CANCELED || projectStatus == ProjectStatus.COMPLETED) {
      throw new BusinessRuleException(ProjectsErrorCodes.ENROLLMENT_PROJECT_UNAVAILABLE);
    }
  }

  /**
   * Validates whether a former student's area of expertise matches any of the project's areas of
   * expertise.
   *
   * @param formerStudentAreaOfExpertise the area of expertise associated with the former student
   * @param projectAreasOfExpertise the list of areas of expertise required by the project
   * @throws BusinessRuleException if there is no match between the student's area and the project's
   *     areas
   */
  public void validateAreaMatch(
      AreaOfExpertise formerStudentAreaOfExpertise, List<AreaOfExpertise> projectAreasOfExpertise) {
    if (formerStudentAreaOfExpertise == null
        || projectAreasOfExpertise == null
        || projectAreasOfExpertise.stream()
            .map(AreaOfExpertise::getId)
            .noneMatch(formerStudentAreaOfExpertise.getId()::equals)) {
      throw new BusinessRuleException(ProjectsErrorCodes.ENROLLMENT_AREA_OF_EXPERTISE_MISMATCH);
    }
  }

  private void collectValidationProblems() {
    validateIdField(id);
    if (entityId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_CREATED_BY_BLANK);
    }
    if (StringUtils.isNotEmpty(description) && description.length() > 4000) {
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
