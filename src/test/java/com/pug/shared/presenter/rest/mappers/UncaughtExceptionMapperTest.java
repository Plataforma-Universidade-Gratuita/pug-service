package com.pug.shared.presenter.rest.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.shared.errors.ErrorCodes;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.rest.ApiEnvelope;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UncaughtExceptionMapperTest {

  @Test
  void mapsTo500WithTranslatedMessageAndEmptyDetails() {
    var mapper = new UncaughtExceptionMapper();
    mapper.i18n =
        new I18n() {
          @Override
          public String t(String key, Object... args) {
            return "Internal error";
          }
        };

    Response r = mapper.toResponse(new RuntimeException("boom"));

    assertEquals(500, r.getStatus());
    assertEquals(MediaType.APPLICATION_JSON_TYPE, r.getMediaType());

    assertTrue(r.getEntity() instanceof ApiEnvelope<?>);
    @SuppressWarnings("unchecked")
    var env = (ApiEnvelope<Void>) r.getEntity();

    assertFalse(env.success());
    assertNull(env.data());
    assertNotNull(env.timestamp());
    assertNotNull(env.error());
    assertEquals(ErrorCodes.INTERNAL_ERROR, env.error().code());
    assertEquals("Internal error", env.error().message());
    assertEquals(Map.of(), env.error().details());
  }
}
