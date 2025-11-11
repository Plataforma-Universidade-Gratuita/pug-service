package com.pug.geo.domain;

import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** City entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class City {

  private final UUID id;
  private final String name;
  private final IbgeCode ibgeCode;

  /**
   * Factory for new cities.
   *
   * @param name the name of the city
   * @param ibgeCode the IBGE code of the city
   * @return the created City instance
   */
  public static City createNew(String name, IbgeCode ibgeCode) {
    City c = new City(null, StringUtils.trim(name), ibgeCode);
    c.validate();
    return c;
  }

  /**
   * Behavior: change the city name.
   *
   * @param newName the new name of the city
   * @return the updated City instance
   */
  public City changeName(String newName) {
    City c = this.toBuilder().name(StringUtils.trim(newName)).build();
    c.validate();
    return c;
  }

  /**
   * Behavior: change IBGE code of the city.
   *
   * @param newCode the new IBGE code of the city
   * @return the updated City instance
   */
  public City changeIbgeCode(IbgeCode newCode) {
    City c = this.toBuilder().ibgeCode(newCode).build();
    c.validate();
    return c;
  }

  /**
   * Validates the City instance.
   *
   * <p>Checks that the name is not null, not blank, and does not exceed 100 characters. Also checks
   * that the IBGE code is not null.
   *
   * @throws AppValidationException if any validation fails
   */
  private void validate() {
    if (StringUtils.isEmpty(name)) {
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
   * Builder class for City.
   *
   * <p>Overrides the build method to include validation.
   */
  public static class CityBuilder {
    /**
     * Builds the City instance with validation.
     *
     * @return the built City instance
     */
    public City build() {
      City c = new City(id, StringUtils.trim(name), ibgeCode);
      c.validate();
      return c;
    }
  }
}
