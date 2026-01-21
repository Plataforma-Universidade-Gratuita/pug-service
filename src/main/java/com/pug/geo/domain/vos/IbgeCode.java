package com.pug.geo.domain.vos;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Value object representing a city's IBGE code.
 *
 * @param code The IBGE code, which must be a 7-digit string.
 */
public record IbgeCode(String code) {

  /**
   * Compact constructor that validates the IBGE code.
   *
   * @param code The IBGE code to be validated.
   * @throws AppValidationException if the code is invalid, potentially containing multiple
   *     problems.
   */
  public IbgeCode {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (StringUtils.isEmpty(code)) {
      problems.add(
          new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_BLANK, "ibgeCode"));
    } else {
      if (code.length() != 7 || !code.chars().allMatch(Character::isDigit)) {
        problems.add(
            new AppValidationException.Problem(GeoErrorCodes.INVALID_IBGE_CODE_FORMAT, "ibgeCode"));
      }
    }

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
  }

  /**
   * Returns the string representation of the IBGE code.
   *
   * @return the IBGE code as a string.
   */
  @Override
  public @NotNull String toString() {
    return code;
  }
}
