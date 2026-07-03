/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.exceptions;

import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.GenericFieldErrorCodes;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serial;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * Exception thrown to indicate that application-level or domain-level validation has failed.
 *
 * <p>Typically, this exception is thrown when a domain entity extending {@link DomainError}
 * accumulates one or more validation failures. By throwing this exception, the current business
 * flow is halted, allowing a global exception handler (e.g., in the API layer) to intercept it and
 * translate the encapsulated {@link GenericFieldErrorCodes} into a structured, localized error
 * response (such as HTTP 400 Bad Request or 422 Unprocessable Entity).
 */
@Getter
public final class AppValidationException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  @SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
  private final transient List<GenericFieldErrorCodes> fieldErrors;

  /**
   * Constructs an {@code AppValidationException} containing multiple validation errors.
   *
   * <p>To ensure consistency, the exception requires at least one validation error to be present.
   * The provided list is internally wrapped in an unmodifiable collection to preserve immutability.
   *
   * @param fieldErrors a {@link List} of {@link GenericFieldErrorCodes} detailing the validation
   *     failures
   * @throws IllegalArgumentException if the provided {@code fieldErrors} list is null or empty
   */
  public AppValidationException(List<GenericFieldErrorCodes> fieldErrors) {
    super("Application validation failed.");
    if (CollectionUtils.isEmpty(fieldErrors)) {
      throw new IllegalArgumentException(
          "AppValidationException must contain at least one problem.");
    }
    this.fieldErrors = Collections.unmodifiableList(fieldErrors);
  }

  /**
   * Convenience constructor for throwing an {@code AppValidationException} due to a single
   * validation failure.
   *
   * <p>Automatically wraps the provided error code in an immutable list.
   *
   * @param code the specific {@link GenericFieldErrorCodes} representing the validation failure
   * @throws IllegalArgumentException if the provided {@code code} is null
   */
  public AppValidationException(GenericFieldErrorCodes code) {
    this(List.of(code));
  }
}
