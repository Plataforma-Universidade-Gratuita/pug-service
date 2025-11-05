package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConstraintViolationExceptionMapperTest {

  private ConstraintViolationExceptionMapper mapper;
  private I18n i18n;

  @BeforeEach
  public void setup() {
    i18n = mock(I18n.class);
    mapper = new ConstraintViolationExceptionMapper();
    mapper.i18n = i18n;
  }

  @Test
  public void testToResponse_withValidations() {
    Set<ConstraintViolation<Object>> violations = new HashSet<>();
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);
    when(violation.getPropertyPath()).thenReturn(propertyPath);
    when(propertyPath.toString()).thenReturn("username");
    when(violation.getMessage()).thenReturn("Username is required");

    violations.add(violation);

    ConstraintViolationException exception = new ConstraintViolationException(violations);

    when(i18n.t(ErrorCodes.VALIDATION_ERROR.getBundleKey())).thenReturn("Validation failed");

    Response response = mapper.toResponse(exception);

    assertEquals(422, response.getStatus(), "Response status should be 422");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(
        ErrorCodes.VALIDATION_ERROR.toString(), envelope.error().code(), "Error code should match");
    assertEquals(
        "Validation failed",
        envelope.error().message(),
        "The message should be correctly translated");

    assertEquals(1, envelope.error().details().get("count"), "Violation count should be 1");
    assertNotNull(envelope.error().details().get("violations"), "Violations should not be null");
    assertTrue(
        ((ArrayList) envelope.error().details().get("violations")).size() > 0,
        "There should be at least one violation");
  }

  @Test
  public void testToResponse_withEmptyViolations() {
    ConstraintViolationException exception = new ConstraintViolationException(new HashSet<>());

    when(i18n.t(ErrorCodes.VALIDATION_ERROR.getBundleKey())).thenReturn("Validation failed");

    Response response = mapper.toResponse(exception);

    assertEquals(422, response.getStatus(), "Response status should be 422");

    ApiEnvelope envelope = (ApiEnvelope) response.getEntity();
    assertEquals(
        ErrorCodes.VALIDATION_ERROR.toString(), envelope.error().code(), "Error code should match");
    assertEquals(
        "Validation failed",
        envelope.error().message(),
        "The message should be correctly translated");

    assertEquals(0, envelope.error().details().get("count"), "Violation count should be 0");
    assertNotNull(envelope.error().details().get("violations"), "Violations should not be null");
    assertTrue(
        ((ArrayList) envelope.error().details().get("violations")).isEmpty(),
        "Violations should be empty");
  }
}
