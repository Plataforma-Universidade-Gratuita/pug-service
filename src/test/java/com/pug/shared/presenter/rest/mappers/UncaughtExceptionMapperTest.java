package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UncaughtExceptionMapperTest {

  private UncaughtExceptionMapper mapper;
  private I18n i18n;

  @BeforeEach
  public void setup() {
    i18n = mock(I18n.class);
    mapper = new UncaughtExceptionMapper();
    mapper.i18n = i18n;
  }

  @Test
  public void testToResponse_withThrowable() {
    Throwable exception = new RuntimeException("Unexpected error");

    String expectedMessage = "An internal server error occurred";
    when(i18n.t(ErrorCodes.INTERNAL_ERROR.getBundleKey())).thenReturn(expectedMessage);

    Response response = mapper.toResponse(exception);

    assertEquals(500, response.getStatus(), "Response status should be 500");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(
        ErrorCodes.INTERNAL_ERROR.toString(),
        envelope.error().code(),
        "Error code should be INTERNAL_ERROR");
    assertEquals(
        expectedMessage,
        envelope.error().message(),
        "The error message should be correctly translated");
    assertNotNull(envelope.error().details(), "Details should be present in the error response");
    assertTrue(envelope.error().details().isEmpty(), "Details map should be empty");
  }

  @Test
  public void testToResponse_withNullThrowable() {
    Throwable exception = null;

    String expectedMessage = "An internal server error occurred";
    when(i18n.t(ErrorCodes.INTERNAL_ERROR.getBundleKey())).thenReturn(expectedMessage);

    Response response = mapper.toResponse(exception);

    assertEquals(500, response.getStatus(), "Response status should be 500");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(
        ErrorCodes.INTERNAL_ERROR.toString(),
        envelope.error().code(),
        "Error code should be INTERNAL_ERROR");
    assertEquals(
        expectedMessage,
        envelope.error().message(),
        "The error message should be correctly translated");
    assertNotNull(envelope.error().details(), "Details should be present in the error response");
    assertTrue(envelope.error().details().isEmpty(), "Details map should be empty");
  }
}
