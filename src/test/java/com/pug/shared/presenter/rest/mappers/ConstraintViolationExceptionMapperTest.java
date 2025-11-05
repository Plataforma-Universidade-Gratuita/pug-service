package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConstraintViolationExceptionMapperTest {

    @Mock
    private I18n i18n;

    @InjectMocks
    private ConstraintViolationExceptionMapper exceptionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToResponse_withViolations() {
        String field = "username";
        String message = "Username cannot be empty";

        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        Path propertyPath = mock(Path.class);
        when(propertyPath.toString()).thenReturn(field);
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn(message);

        ConstraintViolationException ex = new ConstraintViolationException(
                Collections.singleton(violation)
        );

        String errorMessage = "Validation failed";
        when(i18n.t(ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR))).thenReturn(errorMessage);

        Response response = exceptionMapper.toResponse(ex);

        assertEquals(422, response.getStatus(), "The response status should be 422");
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType(), "The response type should be JSON");

        ApiEnvelope<?> apiEnvelope = (ApiEnvelope<?>) response.getEntity();
        assertFalse(apiEnvelope.success(), "The success flag should be false for validation errors");
        assertEquals(ErrorCodes.VALIDATION_ERROR, apiEnvelope.error().code(), "Error code should match");
        assertEquals(errorMessage, apiEnvelope.error().message(), "Error message should match");
        assertTrue(apiEnvelope.error().details().containsKey("count"), "The details should contain count");
        assertTrue(apiEnvelope.error().details().containsKey("violations"), "The details should contain violations");

        List<Map<String, Object>> violationsDetails = (List<Map<String, Object>>) apiEnvelope.error().details().get("violations");
        assertFalse(violationsDetails.isEmpty(), "Violations details should not be empty");
        assertEquals(field, violationsDetails.getFirst().get("field"), "Field name should match");
        assertEquals(message, violationsDetails.getFirst().get("message"), "Violation message should match");
    }

    @Test
    void testToResponse_withEmptyViolations() {
        ConstraintViolationException ex = new ConstraintViolationException(Collections.emptySet());

        String errorMessage = "Validation failed";
        when(i18n.t(ErrorCodes.bundleKey(ErrorCodes.VALIDATION_ERROR))).thenReturn(errorMessage);

        Response response = exceptionMapper.toResponse(ex);

        assertEquals(422, response.getStatus(), "The response status should be 422");
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType(), "The response type should be JSON");

        ApiEnvelope<?> apiEnvelope = (ApiEnvelope<?>) response.getEntity();
        assertFalse(apiEnvelope.success(), "The success flag should be false for validation errors");
        assertEquals(ErrorCodes.VALIDATION_ERROR, apiEnvelope.error().code(), "Error code should match");
        assertEquals(errorMessage, apiEnvelope.error().message(), "Error message should match");
        assertTrue(apiEnvelope.error().details().containsKey("count"), "The details should contain count");
        assertEquals(0, apiEnvelope.error().details().get("count"), "The violation count should be zero");
    }
}
