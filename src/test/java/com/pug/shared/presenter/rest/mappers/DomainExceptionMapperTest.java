package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.domain.exceptions.DomainException;
import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DomainExceptionMapperTest {

  static final class TestDomainException extends DomainException {
    public TestDomainException(String code) {
      super(code);
    }
  }

  @Test
  void mapsKnownCodeViaBundleKeyToTranslatedMessage() {
    var mapper = new DomainExceptionMapper();
    mapper.i18n =
        new I18n() {
          @Override
          public String t(String key, Object... args) {
            return "tr:" + key;
          }
        };

    Response r = mapper.toResponse(new TestDomainException(ErrorCodes.VALIDATION_ERROR));

    assertEquals(400, r.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());
    assertTrue(r.getEntity() instanceof ApiEnvelope<?>);

    @SuppressWarnings("unchecked")
    var env = (ApiEnvelope<Void>) r.getEntity();
    assertFalse(env.success());
    assertNull(env.data());
    assertNotNull(env.timestamp());
    assertEquals(ErrorCodes.VALIDATION_ERROR, env.error().code());
    assertEquals("tr:error.validation", env.error().message());
    assertEquals(Map.of(), env.error().details());
  }

  @Test
  void fallsBackToCodeWhenNotMapped() {
    var mapper = new DomainExceptionMapper();
    mapper.i18n =
        new I18n() {
          @Override
          public String t(String key, Object... args) {
            return "tr:" + key;
          }
        };

    Response r = mapper.toResponse(new TestDomainException("error.x"));

    assertEquals(400, r.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());

    @SuppressWarnings("unchecked")
    var env = (ApiEnvelope<Void>) r.getEntity();
    assertFalse(env.success());
    assertNull(env.data());
    assertNotNull(env.timestamp());
    assertEquals("error.x", env.error().code());
    assertEquals("tr:error.x", env.error().message());
    assertEquals(Map.of(), env.error().details());
  }
}
