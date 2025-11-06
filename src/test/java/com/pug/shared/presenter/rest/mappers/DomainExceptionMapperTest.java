package com.pug.shared.presenter.rest.mappers;

import com.pug.helpers.TestErrorCodes;
import com.pug.shared.exceptions.DomainException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DomainExceptionMapperTest {

  private DomainExceptionMapper mapper;
  private I18n i18n;

  @BeforeEach
  public void setup() {
    i18n = mock(I18n.class);
    mapper = new DomainExceptionMapper();
    mapper.i18n = i18n;
  }

  @Test
  public void testToResponse_withDomainException() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;
    Map<String, Object> details = new HashMap<>();
    details.put("field", "username");
    details.put("message", "Field 'username' is required");

    DomainException exception = mock(DomainException.class);
    when(exception.code()).thenReturn(expectedCode);
    when(exception.getDetails()).thenReturn(details);
    when(i18n.translation(expectedCode.getBundleKey())).thenReturn("Validation failed");

    Response response = mapper.toResponse(exception);

    assertEquals(400, response.getStatus(), "Response status should be 400");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(expectedCode.toString(), envelope.error().code(), "Error code should match");
    assertEquals(
            "Validation failed",
            envelope.error().message(),
            "The message should be correctly translated");

    assertEquals(2, envelope.error().details().size(), "Details should contain 2 elements");
    assertEquals(
            "username",
            envelope.error().details().get("field"),
            "Field 'username' should be in the details");
    assertEquals(
            "Field 'username' is required",
            envelope.error().details().get("message"),
            "The message should be correctly in details");
  }

  @Test
  public void testToResponse_withNullDetails() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_FORMAT;
    DomainException exception = mock(DomainException.class);
    when(exception.code()).thenReturn(expectedCode);
    when(exception.getDetails()).thenReturn(null);
    when(i18n.translation(expectedCode.getBundleKey())).thenReturn("Validation failed");

    Response response = mapper.toResponse(exception);

    assertEquals(400, response.getStatus(), "Response status should be 400");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(expectedCode.toString(), envelope.error().code(), "Error code should match");
    assertEquals(
            "Validation failed",
            envelope.error().message(),
            "The message should be correctly translated");

    assertNotNull(envelope.error().details(), "Details should not be null");
    assertTrue(envelope.error().details().isEmpty(), "Details should be empty when null is passed");
  }

  @Test
  public void testToResponse_withEmptyDetails() {
    TestErrorCodes expectedCode = TestErrorCodes.INVALID_DATA;
    Map<String, Object> emptyDetails = new HashMap<>();

    DomainException exception = mock(DomainException.class);
    when(exception.code()).thenReturn(expectedCode);
    when(exception.getDetails()).thenReturn(emptyDetails);
    when(i18n.translation(expectedCode.getBundleKey())).thenReturn("Validation failed");

    Response response = mapper.toResponse(exception);

    assertEquals(400, response.getStatus(), "Response status should be 400");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(expectedCode.toString(), envelope.error().code(), "Error code should match");
    assertEquals(
            "Validation failed",
            envelope.error().message(),
            "The message should be correctly translated");

    assertNotNull(envelope.error().details(), "Details should not be null");
    assertTrue(
            envelope.error().details().isEmpty(), "Details should be empty when empty map is passed");
  }
}
