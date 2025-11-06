package com.pug.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/** Validator for UUIDv7. */
public final class UuidV7ForUuid implements ConstraintValidator<UuidV7, UUID> {
  /**
   * Checks if the given UUID is a valid UUIDv7.
   *
   * @param value the UUID to validate
   * @param ctx the constraint validator context
   * @return true if valid, false otherwise
   */
  @Override
  public boolean isValid(UUID value, ConstraintValidatorContext ctx) {
    return value == null || value.version() == 7;
  }
}
