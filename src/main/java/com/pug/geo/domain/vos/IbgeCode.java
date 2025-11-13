package com.pug.geo.domain.vos;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing a city's IBGE code.
 *
 * @param code the IBGE code string, must be exactly 7 characters long.
 */
public record IbgeCode(String code) {

  /**
   * Constructs an IbgeCode after validating the input code.
   *
   * @param code the IBGE code string
   * @throws AppValidationException if the code is null, not exactly 7 characters long or contains
   *     non-digit characters
   */
  public IbgeCode {
    if (code == null) {
      throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE_BLANK);
    }
    if (code.length() != 7 || !code.chars().allMatch(Character::isDigit)) {
      throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE_FORMAT);
    }
  }

  /**
   * Returns the string representation of the IBGE code.
   *
   * @return the IBGE code as a string
   */
  @Override
  public @NotNull String toString() {
    return code;
  }
}
