package com.pug.geo.domain.records;

import static org.junit.jupiter.api.Assertions.*;

import com.pug.geo.domain.errors.GeoErrorCodes;
import com.pug.shared.exceptions.AppValidationException;
import org.junit.jupiter.api.Test;

public class IBGECodeTest {

  @Test
  public void testValidIBGECode() {
    String validCode = "1234567";

    IBGECode ibgeCode = new IBGECode(validCode);

    assertEquals(validCode, ibgeCode.toString(), "The code should be correctly stored.");
  }

  @Test
  public void testInvalidIBGECode_nullCode() {
    Exception exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              new IBGECode(null);
            });

    assertEquals(
        GeoErrorCodes.INVALID_IBGE_CODE.toString(),
        exception.getMessage(),
        "The exception should have the correct error message.");
  }

  @Test
  public void testInvalidIBGECode_invalidLength() {
    String invalidCode = "12345";

    Exception exception =
        assertThrows(
            AppValidationException.class,
            () -> {
              new IBGECode(invalidCode);
            });

    assertEquals(
        GeoErrorCodes.INVALID_IBGE_CODE.toString(),
        exception.getMessage(),
        "The exception should have the correct error message.");
  }

  @Test
  public void testToString() {
    String validCode = "1234567";
    IBGECode ibgeCode = new IBGECode(validCode);

    String result = ibgeCode.toString();

    assertEquals(validCode, result, "The toString() method should return the code.");
  }
}
