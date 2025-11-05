package com.pug.geo.domain.records;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.jetbrains.annotations.NotNull;

public record IBGECode(String code) {

  public IBGECode {
    if (code == null || code.length() != 7) {
      throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE);
    }
  }

  @Override
  public @NotNull String toString() {
    return code;
  }
}
