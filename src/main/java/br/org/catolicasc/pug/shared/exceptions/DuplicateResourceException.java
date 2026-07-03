/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.exceptions;

import br.org.catolicasc.pug.shared.domain.enums.GenericCodes;
import lombok.Getter;

/**
 * Exception thrown when attempting to create or update a resource that violates a unique constraint
 * or business uniqueness rule (e.g., "A user with this CPF already exists").
 *
 * <p>This represents a state conflict within the system rather than a syntactic validation error.
 * It typically maps to an HTTP 409 (Conflict) response, returning a localized message to the client
 * indicating the nature of the duplication.
 */
@Getter
public class DuplicateResourceException extends RuntimeException {

  private final GenericCodes code;

  /**
   * Constructs a new {@code DuplicateResourceException}.
   *
   * @param code The specific {@link GenericCodes} indicating which resource already exists (e.g.,
   *     {@code IdentityErrorCodes.USER_ALREADY_EXISTS}).
   */
  public DuplicateResourceException(GenericCodes code) {
    super(code.getBundleKey());
    this.code = code;
  }
}
