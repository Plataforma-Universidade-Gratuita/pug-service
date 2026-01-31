package com.pug.geo.service.utils;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.shared.utils.StringUtils;

/** Utility class for processing City DTO inputs. */
public class CityProcessor {

  /**
   * Helper method to process DTO input and build a new City domain object.
   *
   * <p>The returned City object may contain validation errors. Check {@code city.hasErrors()}.
   *
   * @param name The city name from DTO.
   * @param ibgeCodeString The IBGE code string from DTO.
   * @return The constructed City domain object.
   */
  public static City processCreateInput(String name, String ibgeCodeString) {
    IbgeCode ibgeCodeVo = IbgeCode.factory(ibgeCodeString);
    return City.factory(name, ibgeCodeVo);
  }

  /**
   * Helper method to process DTO input and update an existing City domain object.
   *
   * <p>Only fields provided (not null/empty) will be updated. The returned City object may contain
   * validation errors if the new values are invalid.
   *
   * @param existingCity The existing city to be updated.
   * @param name The city name from DTO (can be null for no change).
   * @param ibgeCodeString The IBGE code string from DTO (can be null for no change).
   * @return The updated City domain object.
   */
  public static City processUpdateInput(City existingCity, String name, String ibgeCodeString) {

    City updatedCity = existingCity;

    if (!StringUtils.isEmpty(name)) {
      updatedCity = updatedCity.changeName(name);
    }

    if (!StringUtils.isEmpty(ibgeCodeString)) {
      IbgeCode newIbgeCode = IbgeCode.factory(ibgeCodeString);
      updatedCity = updatedCity.changeIbgeCode(newIbgeCode);
    }

    return updatedCity;
  }
}
