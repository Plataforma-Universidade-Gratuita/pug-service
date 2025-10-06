package com.pug.shared.mappers;

import com.pug.shared.i18n.I18n;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UncaughtExceptionMapperTest {

    @SuppressWarnings("unchecked")
    private static <T> T get(Object target, String field) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    private static void set(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void mapsThrowableTo500Envelope() throws Exception {
        var mapper = new UncaughtExceptionMapper();

        var headers = mock(HttpHeaders.class);
        var i18n = mock(I18n.class);

        when(i18n.resolve(headers)).thenReturn(Locale.ENGLISH);
        when(i18n.msg("errors.INTERNAL_ERROR", Locale.ENGLISH)).thenReturn("Internal error");

        set(mapper, "headers", headers);
        set(mapper, "i18n", i18n);

        Response r = mapper.toResponse(new RuntimeException("boom"));

        assertEquals(500, r.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());
        assertNotNull(r.getEntity());

        Object apiResponse = r.getEntity();
        Boolean success = get(apiResponse, "success");
        Object data = get(apiResponse, "data");
        Object apiError = get(apiResponse, "error");

        assertEquals(Boolean.FALSE, success);
        assertNull(data);
        assertNotNull(apiError);

        String code = get(apiError, "code");
        String message = get(apiError, "message");
        Map<String, Object> details = get(apiError, "details");

        assertEquals("INTERNAL_ERROR", code);
        assertEquals("Internal error", message);
        assertTrue(details.isEmpty());

        verify(i18n).resolve(headers);
        verify(i18n).msg("errors.INTERNAL_ERROR", Locale.ENGLISH);
        verifyNoMoreInteractions(i18n);
    }
}
