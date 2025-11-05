package com.pug.geo.domain;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.geo.domain.records.IBGECode;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class City {

  private final UUID id;
  private final String name;
  private final IBGECode ibgeCode;

  private void validate() {
    if (name == null || name.isBlank())
      throw new AppValidationException(GeoErrorCodes.INVALID_CITY_NAME_BLANK);
    if (name.length() > 100)
      throw new AppValidationException(GeoErrorCodes.INVALID_CITY_NAME_TOOLONG);
    if (ibgeCode == null) throw new AppValidationException(GeoErrorCodes.INVALID_IBGE_CODE);
  }

  public static class CityBuilder {
    public City build() {
      City c = new City(id, name, ibgeCode);
      c.validate();
      return c;
    }
  }
}
