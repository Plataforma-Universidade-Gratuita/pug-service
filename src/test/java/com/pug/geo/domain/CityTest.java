package com.pug.geo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.geo.domain.records.IBGECode;
import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

public class CityTest {

  @Test
  public void testCityCreation_withValidData() {
    City city = City.builder().name("São Paulo").ibgeCode(new IBGECode("3550308")).build();

    assertEquals("São Paulo", city.getName());
    assertEquals(new IBGECode("3550308"), city.getIbgeCode());
  }

  @Test
  public void testCityCreation_withNullName() {
    AppValidationException exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              City.builder().name(null).ibgeCode(new IBGECode("3550308")).build();
            });

    assertEquals(GeoErrorCodes.INVALID_CITY_NAME_BLANK.toString(), exception.getMessage());
  }

  @Test
  public void testCityCreation_withBlankName() {
    AppValidationException exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              City.builder().name(" ").ibgeCode(new IBGECode("3550308")).build();
            });

    assertEquals(GeoErrorCodes.INVALID_CITY_NAME_BLANK.toString(), exception.getMessage());
  }

  @Test
  public void testCityCreation_withTooLongName() {
    String longName = "A".repeat(101);
    AppValidationException exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              City.builder().name(longName).ibgeCode(new IBGECode("3550308")).build();
            });

    assertEquals(GeoErrorCodes.INVALID_CITY_NAME_TOOLONG.toString(), exception.getMessage());
  }

  @Test
  public void testCityCreation_withNullIBGECode() {
    AppValidationException exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              City.builder().name("São Paulo").ibgeCode(null).build();
            });

    assertEquals(GeoErrorCodes.INVALID_IBGE_CODE.toString(), exception.getMessage());
  }

  @Test
  public void testCityCreation_withInvalidIBGECode() {
    AppValidationException exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              City.builder().name("São Paulo").ibgeCode(new IBGECode("355030")).build();
            });

    assertEquals(GeoErrorCodes.INVALID_IBGE_CODE.toString(), exception.getMessage());
  }
}
