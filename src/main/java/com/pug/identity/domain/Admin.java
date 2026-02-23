package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.domain.enums.SharedErrorCodes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Admin entity aggregate.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Admin extends DomainError {
  UUID accountId;
  OffsetDateTime grantedAt;
  Campi campus;

  @Builder(toBuilder = true)
  private Admin(UUID accountId, OffsetDateTime grantedAt, Campi campus) {
    this.accountId = accountId;
    this.grantedAt = grantedAt;
    this.campus = campus;
  }

  /**
   * Factory for new Admin.
   *
   * @param accountId the ID of the Account associated with the Admin
   * @param campus the campus where the admin comes from.
   * @return new Admin instance (may contain errors)
   */
  public static Admin factory(UUID accountId, Campi campus) {
    Admin admin = Admin.builder().accountId(accountId).campus(campus).grantedAt(OffsetDateTime.now()).build();
    admin.collectValidationProblems();
    return admin;
  }

  /**
   * Behavior: Change the campus at which the admin works at.
   *
   * @param newCampus the new campus to set
   * @return a new Admin instance with the updated campus
   */
  public Admin changeCampus(Campi newCampus) {
    if (campus == newCampus) {
      return this;
    }
    var updatedAdmin = this.toBuilder().campus(newCampus).build();
    updatedAdmin.collectValidationProblems();
    return updatedAdmin;
  }

  /**
   * Validates the Admin instance.
   */
  private void collectValidationProblems() {
    validateForeignKeyField(accountId, "accountId");
    if (grantedAt == null) {
      addError(new Problem(IdentityErrorCodes.INVALID_GRANTED_AT_BLANK));
    }
    if (campus == null) {
      addError(new Problem(SharedErrorCodes.INVALID_CAMPUS_BLANK));
    }
  }
}
