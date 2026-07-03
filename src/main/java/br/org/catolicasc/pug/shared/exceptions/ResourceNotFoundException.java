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
 * Exception thrown when a requested domain resource or aggregate root cannot be found in the
 * underlying data store or external system.
 *
 * <p>This exception abstracts away the underlying persistence mechanism (e.g., database lookup
 * failure) and translates it into a standard domain-level error. It typically maps to an HTTP 404
 * (Not Found) response, delivering a localized message indicating which entity type was not found.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

  private final GenericCodes code;

  /**
   * Constructs a new {@code ResourceNotFoundException}.
   *
   * @param code The specific {@link GenericCodes} indicating the type of resource that was not
   *     found (e.g., {@code GeoErrorCodes.CITY_NOT_FOUND}).
   */
  public ResourceNotFoundException(GenericCodes code) {
    super(code.getBundleKey());
    this.code = code;
  }
}
