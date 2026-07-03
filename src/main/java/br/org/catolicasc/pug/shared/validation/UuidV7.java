/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Custom Jakarta Bean Validation constraint to ensure a given identifier is strictly a UUID version
 * 7.
 *
 * <p>The platform standardizes on UUIDv7 for time-ordered database indexing. This annotation can be
 * applied to API request payloads (DTOs) to validate that clients are supplying the correctly
 * formatted version of UUID before the request reaches the domain layer.
 *
 * <p>Supported types are {@link String} and {@link java.util.UUID}. <i>Note:</i> Null or empty
 * values are considered valid. Use {@code @NotNull} or {@code @NotBlank} in conjunction with this
 * annotation if the field is mandatory.
 */
@Target({FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {UuidV7ForUuid.class, UuidV7ForString.class})
public @interface UuidV7 {

  /**
   * The default message interpolation key. Resolves to the localized message defined in {@code
   * ValidationMessages.properties}.
   *
   * @return the validation message key
   */
  String message() default "{error.validation.uuid.v7}";

  /**
   * Defines the validation groups this constraint belongs to.
   *
   * @return an array of validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Defines the payload associated with this constraint, used by clients of the Bean Validation API
   * to assign custom metadata.
   *
   * @return an array of payload classes
   */
  Class<? extends Payload>[] payload() default {};
}
