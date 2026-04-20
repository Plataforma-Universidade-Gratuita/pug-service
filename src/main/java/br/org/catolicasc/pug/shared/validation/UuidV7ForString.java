package br.org.catolicasc.pug.shared.validation;

import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/**
 * Constraint validator implementation for evaluating {@link String} fields annotated with {@link
 * UuidV7}.
 *
 * <p>This validator ensures that the string can be successfully parsed into a UUID, and that the
 * resulting UUID's version is exactly 7.
 */
public final class UuidV7ForString implements ConstraintValidator<UuidV7, String> {

  /**
   * Evaluates whether the provided string is a valid UUIDv7.
   *
   * <p>Consistent with standard Bean Validation practices, {@code null}, empty, or blank strings
   * are considered valid to allow for optional fields. If the string is present but cannot be
   * parsed into a UUID, or if it parses into a different version (e.g., v4), validation fails.
   *
   * @param value the raw string value to validate
   * @param ctx the context in which the constraint is evaluated
   * @return {@code true} if the string is empty/null or a valid UUIDv7; {@code false} otherwise
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
