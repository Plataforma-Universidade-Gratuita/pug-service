package com.pug.geo.domain.exceptions;

import static com.pug.shared.errors.ErrorCodes.CITY_NOT_FOUND;

import com.pug.shared.errors.DomainException;

public final class CityNotFoundException extends DomainException {
  public CityNotFoundException(String ibgeCode) {
    super(CITY_NOT_FOUND, ibgeCode);
  }
}
