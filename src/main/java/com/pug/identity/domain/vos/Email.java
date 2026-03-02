package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.domain.DomainError;

import java.util.Locale;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * Value object representing an email address. Converted to class to extend DomainError, allowing
 * deferred validation.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Email extends DomainError {

  private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

  String value;

  @Builder(toBuilder = true)
  private Email(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new Email. It normalizes the input (trim and lowercase) and runs
   * validation. It does not throw exceptions immediately but collects them in the problems list.
   *
   * @param rawValue The raw email string.
   * @return The Email instance (which may contain errors).
   */
  public static Email factory(String rawValue) {
    String normalized = rawValue == null ? null : rawValue.trim().toLowerCase(Locale.ROOT);

    Email vo = Email.builder().value(normalized).build();
    vo.collectValidationProblems();
    return vo;
  }

  /** Validates the email format and length, populating the problems list if invalid. */
  private void collectValidationProblems() {
    validateStringField(value, 254L, "email");

    if (!EMAIL_VALIDATOR.isValid(value)) {
      addFieldError(new Problem(IdentityErrorCodes.INVALID_EMAIL_FORMAT));
    }
  }
}
