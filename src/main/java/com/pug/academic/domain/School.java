package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** School entityId aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class School extends DomainError {
  UUID id;
  String name;
  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private School(UUID id, String name, AuditInfo auditInfo) {
    this.id = id;
    this.name = name;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory for new schools.
   *
   * @param name the name of the school
   * @return the created school (may contain errors)
   */
  public static School factory(String name) {
    String trimmedName = StringUtils.trim(name);
    School school =
        School.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .auditInfo(AuditInfo.factory())
            .build();

    school.collectValidationProblems();
    return school;
  }

  /**
   * Behavior: change the school name.
   *
   * @param newName new name for the school
   * @return new school with updated name
   */
  public School changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (name.equals(trimmedName)) {
      return this;
    }
    School updatedSchool = toBuilder().name(trimmedName).auditInfo(auditInfo.update()).build();
    updatedSchool.collectValidationProblems();
    return updatedSchool;
  }

  /** Collects all validation problems for the School instance. */
  private void collectValidationProblems() {
    validateIdField(id);
    validateStringField(name, 100L, "name");
    if (auditInfo == null) {
      addFieldError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
