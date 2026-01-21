package com.pug.shared.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** UUIDv7 validation annotation. */
@Target({FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {UuidV7ForUuid.class, UuidV7ForString.class})
public @interface UuidV7 {
  /**
   * Validation message key. Defaults to "{error.validation.uuid.v7}".
   *
   * @return the validation message key.
   */
  String message() default "{error.validation.uuid.v7}";

  /**
   * Validation groups.
   *
   * @return the validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Payload.
   *
   * @return the payload.
   */
  Class<? extends Payload>[] payload() default {};
}
