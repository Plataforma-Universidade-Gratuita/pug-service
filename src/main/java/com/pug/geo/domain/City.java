package com.pug.geo.domain;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.geo.domain.records.IbgeCode;
import com.pug.shared.exceptions.AppValidationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain entity representing a City with validation logic.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class City {

  private final UUID id;
  private final String name;
  private final IbgeCode ibgeCode;

  private void validate() {
    if (name == null || name.isBlank()) {
      throw new AppValidationException(GeoErrorCodes.INVALID_CITY_NAME_BLANK);
    }
    if (name.length() > 100) {
      throw new AppValidationException(GeoErrorCodes.INVALID_CITY_NAME_TOOLONG);
    }
    if (ibgeCode == null) {
      throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE);
    }
  }

  /**
   * Builder class for City with validation on build.
   */
  public static class CityBuilder {
    /**
     * Builds the City instance after validating its fields.
     *
     * @return Validated City instance
     */
    public City build() {
      City c = new City(id, name, ibgeCode);
      c.validate();
      return c;
    }
  }
}
