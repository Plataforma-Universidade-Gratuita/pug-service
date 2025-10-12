package com.pug.geo.domain;

import com.pug.shared.domain.exceptions.AppValidationException;
import com.pug.shared.errors.ErrorCodes;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public final class City {
  private final UUID id;
  private final String name;
  private final String ibgeCode; // 7 digits

  private void validate() {
    if (name == null || name.isBlank())
      throw new AppValidationException(ErrorCodes.CITY_NAME_REQUIRED);
    if (name.length() > 100) throw new AppValidationException(ErrorCodes.CITY_NAME_TOO_LONG);
    if (ibgeCode == null || !ibgeCode.matches("\\d{7}"))
      throw new AppValidationException(ErrorCodes.CITY_IBGE_INVALID);
  }

  public static class CityBuilder {
    public City build() {
      City c = new City(id, name, ibgeCode);
      c.validate();
      return c;
    }
  }
}
