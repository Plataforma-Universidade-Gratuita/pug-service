package com.pug.geo.domain.records;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing an IBGE code.
 *
 * @param code the IBGE code string, must be exactly 7 characters long.
 */
public record IbgeCode(String code) {

  /**
   * Constructs an IbgeCode after validating the input code.
   *
   * @param code the IBGE code string.
   * @throws AppValidationException if the code is null or not exactly 7 characters long.
   */
  public IbgeCode {
    if (code == null || code.length() != 7) {
      throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE);
    }
  }

  @Override
  public @NotNull String toString() {
    return code;
  }
}
