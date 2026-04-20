package br.org.catolicasc.pug.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

/**
 * Constraint validator implementation for evaluating {@link java.util.UUID} objects annotated with
 * {@link UuidV7}.
 *
 * <p>This is typically used when the framework (like Jackson or JAX-RS) has already deserialized
 * the payload into a UUID object, but we must verify that the underlying version of that UUID is
 * specifically version 7.
 */
public final class UuidV7ForUuid implements ConstraintValidator<UuidV7, UUID> {

  /**
   * Evaluates whether the provided UUID object is version 7.
   *
   * <p>Consistent with standard Bean Validation practices, {@code null} values are considered
   * valid.
   *
   * @param value the UUID object to validate
   * @param ctx the context in which the constraint is evaluated
   * @return {@code true} if the UUID is null or its version is 7; {@code false} otherwise
   */
  @Override
  public boolean isValid(UUID value, ConstraintValidatorContext ctx) {
    return value == null || value.version() == 7;
  }
}
