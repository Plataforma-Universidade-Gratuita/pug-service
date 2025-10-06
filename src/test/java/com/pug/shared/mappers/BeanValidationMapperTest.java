package com.pug.shared.mappers;

import com.pug.shared.i18n.I18n;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BeanValidationMapperTest {

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

    static final class SimplePath implements Path {
        private final String s;
        SimplePath(String s) { this.s = s; }
        @Override public Iterator<Node> iterator() { return Collections.emptyIterator(); }
        @Override public String toString() { return s; }
    }

    private static <T> T getAny(Object target, String... fields) throws Exception {
        for (String f : fields) {
            try {
                return get(target, f);
            } catch (NoSuchFieldException ignored) {}
        }
        return null;
    }

    @Test
    void mapsViolationsTo422Envelope() throws Exception {
        // mapper with injected deps
        var mapper = new BeanValidationMapper();

        var headers = mock(HttpHeaders.class);
        var i18n = mock(I18n.class);

        when(i18n.resolve(headers)).thenReturn(Locale.ENGLISH);
        when(i18n.msg("errors.VALIDATION_ERROR", Locale.ENGLISH))
                .thenReturn("Validation failed");

        set(mapper, "headers", headers);
        set(mapper, "i18n", i18n);

        // two violations
        ConstraintViolation<?> v1 = mock(ConstraintViolation.class);
        when(v1.getPropertyPath()).thenReturn(new SimplePath("query.cnpj"));
        when(v1.getMessage()).thenReturn("must not be blank");

        ConstraintViolation<?> v2 = mock(ConstraintViolation.class);
        when(v2.getPropertyPath()).thenReturn(new SimplePath("id"));
        when(v2.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> set = new LinkedHashSet<>(List.of(v1, v2));
        var ex = new ConstraintViolationException("bad", set);

        // exercise
        Response r = mapper.toResponse(ex);

        // assertions
        assertEquals(422, r.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());
        assertNotNull(r.getEntity());

        // inspect ApiResponse -> ApiError -> details.violations
        Object apiResponse = r.getEntity();
        Object apiError = get(apiResponse, "error");
        assertNotNull(apiError);

        String code = (String) get(apiError, "code");
        String message = (String) getAny(apiError, "message", "localizedMessage", "userMessage");
        Map<String, Object> details = get(apiError, "details");

        assertNotNull(message);
        assertEquals("VALIDATION_ERROR", code);
        assertEquals("Validation failed", message);
        assertTrue(details.containsKey("violations"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> violations = (List<Map<String, Object>>) details.get("violations");
        assertEquals(2, violations.size());
        assertEquals(Map.of("field", "query.cnpj", "message", "must not be blank"), violations.get(0));
        assertEquals(Map.of("field", "id", "message", "must not be null"), violations.get(1));

        // i18n interactions
        verify(i18n).resolve(headers);
        verify(i18n).msg("errors.VALIDATION_ERROR", Locale.ENGLISH);
        verifyNoMoreInteractions(i18n);
    }
}
