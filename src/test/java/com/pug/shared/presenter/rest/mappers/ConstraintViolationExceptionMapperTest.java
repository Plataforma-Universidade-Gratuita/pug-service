package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ConstraintViolationExceptionMapperTest {

  record Dto(@NotNull(message = "bad.input") String x) {}

  record D2(@NotNull(message = "m1") String a, @NotNull(message = "m2") String b) {}

  static Validator validator;

  @BeforeAll
  static void setup() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void mapsViolationsTo422WithDetailsAndTranslatedMessage() {
    var violations = validator.validate(new Dto(null));
    assertFalse(violations.isEmpty());

    var ex = new ConstraintViolationException(violations);
    var mapper = new ConstraintViolationExceptionMapper();
    mapper.i18n =
        new I18n() {
          @Override
          public String t(String key, Object... args) {
            return "tr:" + key;
          }
        };

    Response r = mapper.toResponse(ex);

    assertEquals(422, r.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());
    assertTrue(r.getEntity() instanceof ApiEnvelope<?>);

    @SuppressWarnings("unchecked")
    var env = (ApiEnvelope<Void>) r.getEntity();
    assertFalse(env.success());
    assertNull(env.data());
    assertNotNull(env.timestamp());
    assertEquals(ErrorCodes.VALIDATION_ERROR, env.error().code());
    assertEquals("tr:error.validation", env.error().message());

    Map<String, Object> details = env.error().details();
    assertNotNull(details);
    assertEquals(1, details.get("count"));
    @SuppressWarnings("unchecked")
    var list = (List<Map<String, Object>>) details.get("violations");
    assertEquals(1, list.size());
    assertEquals("bad.input", list.get(0).get("message"));
  }

  @Test
  void multipleViolationsAreListed() {
    Set<ConstraintViolation<D2>> v = validator.validate(new D2(null, null));
    var mapper = new ConstraintViolationExceptionMapper();
    mapper.i18n =
        new I18n() {
          @Override
          public String t(String key, Object... args) {
            return key;
          }
        };
    @SuppressWarnings("unchecked")
    var env =
        (ApiEnvelope<Void>) mapper.toResponse(new ConstraintViolationException(v)).getEntity();

    Map<String, Object> details = env.error().details();
    @SuppressWarnings("unchecked")
    var list = (List<Map<String, Object>>) details.get("violations");
    String m1 = (String) list.get(0).get("message") + (String) list.get(1).get("message");
    assertTrue(m1.contains("m1"));
    assertTrue(m1.contains("m2"));
    assertEquals(2, details.get("count"));
  }
}
