package com.pug.identity.domain;

import com.pug.identity.domain.enums.IdentityFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Immutable Domain Entity representing an Administrator profile.
 * <p>
 * This class maps administrative privileges and location boundaries directly to
 * an existing {@link Account}. It serves as an aggregate for metadata strictly
 * tied to administration rights. It extends {@link DomainError} to accumulate
 * validation failures.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Admin extends DomainError {

  /**
   * The unique identifier of the linked {@link Account}, which acts as the primary key.
   */
  UUID accountId;

  /**
   * The precise timestamp when administrative privileges were granted.
   */
  OffsetDateTime grantedAt;

  /**
   * The specific university campus associated with the administrator's operational scope.
   */
  Campi campus;

  /**
   * Constructs an {@code Admin} instance.
   *
   * @param accountId the linked account's identifier
   * @param grantedAt the time the privileges were assigned
   * @param campus    the assigned campus
   */
  @Builder(toBuilder = true)
  private Admin(UUID accountId, OffsetDateTime grantedAt, Campi campus) {
    this.accountId = accountId;
    this.grantedAt = grantedAt;
    this.campus = campus;
  }

  /**
   * Factory method to create a new {@code Admin} instance.
   * <p>
   * Automatically stamps the creation operation with the current system time
   * and performs full domain validation before returning.
   *
   * @param accountId the UUID of the {@link Account} receiving administrative privileges
   * @param campus    the {@link Campi} designation where the administrator operates
   * @return a newly created and self-validated {@link Admin} instance
   */
  public static Admin factory(UUID accountId, Campi campus) {
    Admin admin =
            Admin.builder().accountId(accountId).campus(campus).grantedAt(OffsetDateTime.now()).build();
    admin.collectValidationProblems();
    return admin;
  }

  /**
   * Updates the campus assignment for the administrator.
   * <p>
   * Since this entity is immutable, this method returns a new {@code Admin} instance
   * reflecting the new campus. The new instance is fully re-validated.
   *
   * @param newCampus the new {@link Campi} to assign
   * @return a new, updated, and validated {@link Admin} instance, or {@code this} if the campus is unchanged
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
   * Evaluates constraints for the Admin entity and aggregates any validation problems.
   * <p>
   * Rules applied:
   * <ul>
   *   <li>Ensures the {@code accountId} is not null (appends {@link IdentityFieldErrorCodes#INVALID_ACCOUNT_ID_BLANK})</li>
   *   <li>Ensures the {@code grantedAt} timestamp is present (appends {@link IdentityFieldErrorCodes#INVALID_GRANTED_AT_BLANK})</li>
   *   <li>Ensures the {@code campus} definition is not null (appends {@link SharedFieldErrorCodes#INVALID_CAMPUS_BLANK})</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (accountId == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK);
    }
    if (grantedAt == null) {
      addFieldError(IdentityFieldErrorCodes.INVALID_GRANTED_AT_BLANK);
    }
    if (campus == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_CAMPUS_BLANK);
    }
  }
}