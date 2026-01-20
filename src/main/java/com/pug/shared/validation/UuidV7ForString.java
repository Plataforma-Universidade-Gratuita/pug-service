package com.pug.shared.validation;

import com.pug.shared.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

/**
 * Validator for UUIDv7 strings.
 */
public final class UuidV7ForString implements ConstraintValidator<UuidV7, String> {
  /**
   * Checks if the given string is a valid UUIDv7.
   *
   * @param value the string to validate
   * @param ctx   the constraint validator context
   * @return true if valid, false otherwise
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext ctx) {
    if (StringUtils.isEmpty(value)) {
      return true;
    }
    try {
      return UUID.fromString(value).version() == 7;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}