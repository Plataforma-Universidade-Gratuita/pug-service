package com.pug.shared.presenter.rest.mappers;

import com.pug.shared.domain.exceptions.DomainException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DomainExceptionMapperTest {

    @Mock
    private I18n i18n;

    @InjectMocks
    private DomainExceptionMapper exceptionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToResponse() {
        String errorCode = "VALIDATION_ERROR";
        String errorMessage = "The provided data is invalid";

        DomainException domainException = mock(DomainException.class);
        when(domainException.code()).thenReturn(errorCode);

        when(i18n.t(ErrorCodes.bundleKey(errorCode))).thenReturn(errorMessage);

        Response response = exceptionMapper.toResponse(domainException);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus(), "The response status should be 400");
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType(), "The response type should be JSON");

        ApiEnvelope<?> apiEnvelope = (ApiEnvelope<?>) response.getEntity();
        assertFalse(apiEnvelope.success(), "The success flag should be false for domain errors");
        assertEquals(errorCode, apiEnvelope.error().code(), "Error code should match");
        assertEquals(errorMessage, apiEnvelope.error().message(), "Error message should match");
        assertTrue(apiEnvelope.error().details().isEmpty(), "Details should be an empty map");
    }
}
