package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.geo.domain.City;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.AppValidationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EntityTest {

  private static String repeat(char c, int n) {
    return String.valueOf(c).repeat(n);
  }

  @Test
  @DisplayName("build succeeds with valid data")
  void build_valid() {
    UUID id = UUID.randomUUID();
    Cnpj cnpj = new Cnpj("04.252.011/0001-10");
    String name = "Entidade X";
    City city = Mockito.mock(City.class);
    String address = "Rua A, 123";

    Entity e = Entity.builder().id(id).cnpj(cnpj).name(name).city(city).address(address).build();

    assertEquals(id, e.getId());
    assertEquals(cnpj, e.getCnpj());
    assertEquals(name, e.getName());
    assertEquals(city, e.getCity());
    assertEquals(address, e.getAddress());
  }

  @Test
  @DisplayName("null CNPJ -> INVALID_CNPJ")
  void nullCnpj_fails() {
    City city = Mockito.mock(City.class);
    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () -> Entity.builder().id(UUID.randomUUID()).cnpj(null).name("Ok").city(city).build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_CNPJ);
  }

  @Test
  @DisplayName("null name -> INVALID_NAME_BLANK")
  void nullName_fails() {
    City city = Mockito.mock(City.class);
    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () ->
                Entity.builder()
                    .id(UUID.randomUUID())
                    .cnpj(new Cnpj("04.252.011/0001-10"))
                    .name(null)
                    .city(city)
                    .build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_NAME_BLANK);
  }

  @Test
  @DisplayName("blank name -> INVALID_NAME_BLANK")
  void blankName_fails() {
    City city = Mockito.mock(City.class);
    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () ->
                Entity.builder()
                    .id(UUID.randomUUID())
                    .cnpj(new Cnpj("04.252.011/0001-10"))
                    .name("   ")
                    .city(city)
                    .build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_NAME_BLANK);
  }

  @Test
  @DisplayName("name > 150 chars -> INVALID_NAME_TOOLONG")
  void longName_fails() {
    City city = Mockito.mock(City.class);
    String tooLong = repeat('A', 151);

    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () ->
                Entity.builder()
                    .id(UUID.randomUUID())
                    .cnpj(new Cnpj("04.252.011/0001-10"))
                    .name(tooLong)
                    .city(city)
                    .build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_NAME_TOOLONG);
  }

  @Test
  @DisplayName("null city -> INVALID_CITY")
  void nullCity_fails() {
    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () ->
                Entity.builder()
                    .id(UUID.randomUUID())
                    .cnpj(new Cnpj("04.252.011/0001-10"))
                    .name("Ok")
                    .city(null)
                    .build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_CITY);
  }

  @Test
  @DisplayName("address > 254 chars -> INVALID_ADDRESS_TOOLONG")
  void longAddress_fails() {
    City city = Mockito.mock(City.class);
    String longAddress = repeat('Z', 255);

    AppValidationException ex =
        assertThrows(
            AppValidationException.class,
            () ->
                Entity.builder()
                    .id(UUID.randomUUID())
                    .cnpj(new Cnpj("04.252.011/0001-10"))
                    .name("Ok")
                    .city(city)
                    .address(longAddress)
                    .build());

    assertErrorCode(ex, PartnerErrorCodes.INVALID_ADDRESS_TOOLONG);
  }

  @Test
  @DisplayName("boundary lengths accepted: name=150, address=254")
  void boundaryLengths_ok() {
    City city = Mockito.mock(City.class);
    String name150 = repeat('N', 150);
    String addr254 = repeat('A', 254);

    assertDoesNotThrow(
        () ->
            Entity.builder()
                .id(UUID.randomUUID())
                .cnpj(new Cnpj("04.252.011/0001-10"))
                .name(name150)
                .city(city)
                .address(addr254)
                .build());
  }

  private static void assertErrorCode(AppValidationException ex, PartnerErrorCodes expected) {
    try {
      var m = ex.getClass().getMethod("getErrorCode");
      Object code = m.invoke(ex);
      assertEquals(expected, code);
    } catch (ReflectiveOperationException ignored) {
      assertNotNull(ex.getMessage());
    }
  }
}
