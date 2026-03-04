package com.pug.identity.domain.vos;

import com.pug.identity.domain.enums.IdentityFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Locale;

/**
 * Immutable Value Object (VO) representing an electronic mail (email) address.
 * <p>
 * Extends {@link DomainError} to encapsulate and accumulate domain validation rules
 * specific to email addresses without throwing immediate exceptions. This allows the
 * domain layer to defer validation and return a comprehensive list of field errors.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Email extends DomainError {

  private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

  /**
   * The normalized (trimmed and lowercased) string representation of the email address.
   */
  String value;

  /**
   * Constructs an {@code Email} instance.
   *
   * @param value the normalized email string
   */
  @Builder(toBuilder = true)
  private Email(String value) {
    this.value = value;
  }

  /**
   * Factory method to create a new {@code Email} instance.
   * <p>
   * The provided raw value is automatically normalized (trimmed and converted to lowercase)
   * before instantiation. The instance is immediately self-validated. Any validation failures
   * are accumulated internally and can be retrieved via {@link #getFieldErrors()}.
   *
   * @param rawValue the raw email string provided by the user or external system
   * @return a self-validated {@link Email} instance
   */
  public static Email factory(String rawValue) {
    String normalized = rawValue == null ? null : rawValue.trim().toLowerCase(Locale.ROOT);

    Email vo = Email.builder().value(normalized).build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   * <p>
   * Business rules applied:
   * <ul>
   *   <li>Must not be null, empty, or whitespace (appends {@link IdentityFieldErrorCodes#INVALID_EMAIL_BLANK})</li>
   *   <li>Must not exceed 254 characters and must pass the Apache Commons Email format validation
   *       (appends {@link IdentityFieldErrorCodes#INVALID_EMAIL_FORMAT})</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (StringUtils.isEmpty(value)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_EMAIL_BLANK);
      return;
    }
    if (value.length() > 254 || !EMAIL_VALIDATOR.isValid(value)) {
      addFieldError(IdentityFieldErrorCodes.INVALID_EMAIL_FORMAT);
    }
  }
}