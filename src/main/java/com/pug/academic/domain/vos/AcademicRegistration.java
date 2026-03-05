package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing a student's Academic Registration identifier.
 * <p>
 * Extends {@link DomainError} to encapsulate and accumulate domain validation rules
 * specific to university registration formats without throwing immediate exceptions.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AcademicRegistration extends DomainError {

  /**
   * The raw string representing the student's registration identifier.
   */
  String value;

  /**
   * Constructs an {@code AcademicRegistration} instance.
   *
   * @param value the registration string
   */
  @Builder(toBuilder = true)
  private AcademicRegistration(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new {@code AcademicRegistration} instance.
   * <p>
   * Automatically trims whitespace from the input and executes validation logic.
   *
   * @param registration the raw registration string
   * @return a self-validated {@link AcademicRegistration} instance
   */
  public static AcademicRegistration factory(String registration) {
    String trimmed = StringUtils.trim(registration);
    AcademicRegistration vo = AcademicRegistration.builder().value(trimmed).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   * <p>
   * Business rules applied:
   * <ul>
   *   <li>Must not be null or empty (appends {@link AcademicFieldErrorCodes#INVALID_REGISTRATION_BLANK})</li>
   *   <li>Must not exceed 15 characters in length (appends {@link AcademicFieldErrorCodes#INVALID_REGISTRATION_TOO_LONG})</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REGISTRATION_BLANK);
      return;
    }
    if (value.length() > 15) {
      addFieldError(AcademicFieldErrorCodes.INVALID_REGISTRATION_TOO_LONG);
    }
  }
}