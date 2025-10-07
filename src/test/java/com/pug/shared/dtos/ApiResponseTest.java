package com.pug.shared.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ApiResponseTest {

  @Test
  void okPopulatesFields() {
    var r = ApiResponse.ok("data");
    assertTrue(r.success());
    assertEquals("data", r.data());
    assertNull(r.error());
    assertNotNull(r.timestamp());
    assertFalse(r.timestamp().isAfter(Instant.now()));
  }

  @Test
  void createdBehavesLikeOk() {
    var r = ApiResponse.created(123);
    assertTrue(r.success());
    assertEquals(123, r.data());
    assertNull(r.error());
    assertNotNull(r.timestamp());
  }

  @Test
  void errorPopulatesFields() {
    var err = new ApiError("C", "m", null);
    var r = ApiResponse.error(err);
    assertFalse(r.success());
    assertNull(r.data());
    assertEquals(err, r.error());
    assertNotNull(r.timestamp());
  }
}
