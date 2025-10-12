package com.pug.shared.presenter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiEnvelopeTest {

  @Test
  void okFactorySetsSuccessDataTimestamp() {
    var env = ApiEnvelope.ok("data");
    assertTrue(env.success());
    assertEquals("data", env.data());
    assertNull(env.error());
    assertNotNull(env.timestamp());
  }

  @Test
  void errorFactorySetsErrorNullDataAndTimestamp() {
    var env = ApiEnvelope.error("code", "msg");
    assertFalse(env.success());
    assertNull(env.data());
    assertNotNull(env.error());
    assertEquals("code", env.error().code());
    assertEquals("msg", env.error().message());
    assertEquals(Map.of(), env.error().details());
    assertNotNull(env.timestamp());
  }

  @Test
  void errorFactoryWithDetailsStoresDetails() {
    var env = ApiEnvelope.error("code", "msg", Map.of("field", "x"));
    assertFalse(env.success());
    assertEquals(Map.of("field", "x"), env.error().details());
  }
}
