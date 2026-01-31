package com.pug.academic.domain.vos;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value Object representing an Academic Registration. Extends DomainError to allow deferred
 * validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AcademicRegistration extends DomainError {

  String value;

  @Builder(toBuilder = true)
  private AcademicRegistration(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new AcademicRegistration.
   *
   * @param registration the raw registration string
   * @return The AcademicRegistration instance (which may contain errors)
   */
  public static AcademicRegistration factory(String registration) {
    String trimmed = StringUtils.trim(registration);
    AcademicRegistration vo = AcademicRegistration.builder().value(trimmed).build();
    vo.validate();
    return vo;
  }

  /** Validates the registration format and length. */
  private void validate() {
    if (StringUtils.isEmpty(value)) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_REGISTRATION_BLANK));
    } else if (value.length() > 15) {
      addError(new AppValidationException.Problem(AcademicErrorCodes.INVALID_REGISTRATION_LENGTH));
    }
  }

  @Override
  public String toString() {
    return value;
  }
}
