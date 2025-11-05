package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UncaughtExceptionMapperTest {

  @Mock private I18n i18n;

  @InjectMocks private UncaughtExceptionMapper exceptionMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testToResponse() {
    Throwable exception = new RuntimeException("Something went wrong");

    String errorMessage = "An unexpected error occurred";
    when(i18n.t(ErrorCodes.bundleKey(ErrorCodes.INTERNAL_ERROR))).thenReturn(errorMessage);

    Response response = exceptionMapper.toResponse(exception);

    assertEquals(
        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
        response.getStatus(),
        "The response status should be 500");
    assertEquals(
        MediaType.APPLICATION_JSON_TYPE,
        response.getMediaType(),
        "The response type should be JSON");

    ApiEnvelope<?> apiEnvelope = (ApiEnvelope<?>) response.getEntity();
    assertFalse(apiEnvelope.success(), "The success flag should be false for internal errors");
    assertEquals(ErrorCodes.INTERNAL_ERROR, apiEnvelope.error().code(), "Error code should match");
    assertEquals(errorMessage, apiEnvelope.error().message(), "Error message should match");
    assertTrue(apiEnvelope.error().details().isEmpty(), "Details should be an empty map");
  }
}
