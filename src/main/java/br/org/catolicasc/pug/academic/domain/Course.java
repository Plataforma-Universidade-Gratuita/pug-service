package br.org.catolicasc.pug.academic.domain;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Domain Entity representing an Academic Course.
 *
 * <p>This class acts as an aggregate root containing the course's unique identifier, its name, and
 * its association with a specific {@link AreaOfExpertise}. It extends {@link DomainError} to
 * accumulate validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Course extends DomainError {

  /** The unique identifier for the course (UUIDv7). */
  UUID id;

  /** The name of the academic course. */
  String name;

  /** The unique identifier of the {@link AreaOfExpertise} that offers this course. */
  UUID areaOfExpertiseId;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code Course} instance.
   *
   * @param id the unique identifier
   * @param name the name of the course
   * @param areaOfExpertiseId the unique identifier of the associated areaOfExpertise
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private Course(UUID id, String name, UUID areaOfExpertiseId, AuditInfo auditInfo) {
    this.id = id;
    this.name = name;
    this.areaOfExpertiseId = areaOfExpertiseId;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code Course} aggregate.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, trims the
   * provided name, initializes standard audit tracking information, and performs a full validation
   * of the aggregate.
   *
   * @param name the name of the course
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise offering the course
   * @return a newly created and self-validated {@link Course} instance
   */
  public static Course factory(String name, UUID areaOfExpertiseId) {
    String trimmedName = StringUtils.trim(name);
    Course course =
        Course.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .areaOfExpertiseId(areaOfExpertiseId)
            .auditInfo(AuditInfo.factory())
            .build();

    course.collectValidationProblems();
    return course;
  }

  /**
   * Updates the course's name.
   *
   * <p>Since this entity is immutable, this method returns a new {@code Course} instance with the
   * updated, trimmed name and a refreshed {@link AuditInfo} timestamp.
   *
   * @param newName the new name for the course
   * @return a new, updated, and validated {@link Course} instance, or {@code this} if the name is
   *     unchanged
   */
  public Course rename(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (name.equals(trimmedName)) {
      return this;
    }
    Course updatedCourse = toBuilder().name(trimmedName).auditInfo(auditInfo.update()).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /**
   * Updates the association of the course to a different areaOfExpertise.
   *
   * @param newAreaOfExpertiseId the unique identifier of the new areaOfExpertise
   * @return a new, updated, and validated {@link Course} instance, or {@code this} if the
   *     areaOfExpertise is unchanged
   */
  public Course moveToAreaOfExpertise(UUID newAreaOfExpertiseId) {
    if (areaOfExpertiseId.equals(newAreaOfExpertiseId)) {
      return this;
    }
    Course updatedCourse =
        toBuilder().areaOfExpertiseId(newAreaOfExpertiseId).auditInfo(auditInfo.update()).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /**
   * Evaluates constraints for the Course aggregate and accumulates any validation problems.
   *
   * <p>Rules applied:
   *
   * <ul>
   *   <li>Validates the UUID (inherited from {@link DomainError})
   *   <li>Validates the entity {@code name} (inherited from {@link DomainError})
   *   <li>Ensures the {@code areaOfExpertiseId} is not null (appends {@link
   *       AcademicFieldErrorCodes#INVALID_AREA_OF_EXPERTISE_BLANK})
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors
   * </ul>
   */
  private void collectValidationProblems() {
    validateIdField(id);
    validateNameField(name);
    if (areaOfExpertiseId == null) {
      addFieldError(AcademicFieldErrorCodes.INVALID_AREA_OF_EXPERTISE_BLANK);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
