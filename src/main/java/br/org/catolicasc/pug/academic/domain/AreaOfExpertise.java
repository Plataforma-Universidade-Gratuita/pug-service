package br.org.catolicasc.pug.academic.domain;

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
 * Immutable Domain Entity representing an Academic AreaOfExpertise.
 *
 * <p>This class acts as an aggregate root representing a university department or educational
 * institution that groups together various {@link Course} entities. It extends {@link DomainError}
 * to accumulate validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class AreaOfExpertise extends DomainError {

  /** The unique identifier for the school (UUIDv7). */
  UUID id;

  /** The name of the academic school. */
  String name;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code AreaOfExpertise} instance.
   *
   * @param id the unique identifier
   * @param name the name of the school
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private AreaOfExpertise(UUID id, String name, AuditInfo auditInfo) {
    this.id = id;
    this.name = name;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code AreaOfExpertise} aggregate.
   *
   * <p>Automatically generates a time-ordered epoch UUID (UUIDv7) for the identifier, trims the
   * provided name, initializes standard audit tracking information, and performs a full validation
   * of the aggregate.
   *
   * @param name the name of the school
   * @return a newly created and self-validated {@link AreaOfExpertise} instance
   */
  public static AreaOfExpertise factory(String name) {
    String trimmedName = StringUtils.trim(name);
    AreaOfExpertise school =
        AreaOfExpertise.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .auditInfo(AuditInfo.factory())
            .build();

    school.collectValidationProblems();
    return school;
  }

  /**
   * Updates the school's name.
   *
   * <p>Since this entity is immutable, this method returns a new {@code AreaOfExpertise} instance
   * with the updated, trimmed name and a refreshed {@link AuditInfo} timestamp.
   *
   * @param newName the new name for the school
   * @return a new, updated, and validated {@link AreaOfExpertise} instance, or {@code this} if the
   *     name is unchanged
   */
  public AreaOfExpertise rename(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (name.equals(trimmedName)) {
      return this;
    }
    AreaOfExpertise updatedSchool =
        toBuilder().name(trimmedName).auditInfo(auditInfo.update()).build();
    updatedSchool.collectValidationProblems();
    return updatedSchool;
  }

  /**
   * Evaluates constraints for the AreaOfExpertise aggregate and accumulates any validation
   * problems.
   *
   * <p>Rules applied:
   *
   * <ul>
   *   <li>Validates the UUID (inherited from {@link DomainError})
   *   <li>Validates the entity {@code name} (inherited from {@link DomainError})
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors
   * </ul>
   */
  private void collectValidationProblems() {
    validateIdField(id);
    validateNameField(name);
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
