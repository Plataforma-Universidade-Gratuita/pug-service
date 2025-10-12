package com.pug.shared.presenter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Test
  void gettersAndDetailsAreDefensiveCopies() {
    var src = new LinkedHashMap<String, Object>();
    src.put("field", "x");
    var e = new ApiError("code.x", "msg", src);

    assertEquals("code.x", e.code());
    assertEquals("msg", e.message());
    assertEquals(Map.of("field", "x"), e.details());

    // mutate source map -> record must not change
    src.put("extra", 1);
    assertEquals(Map.of("field", "x"), e.details());

    // mutate returned details -> internal must not change
    var copy = e.details();
    copy.put("z", 9);
    assertEquals(Map.of("field", "x"), e.details());
  }

  @Test
  void equalsHashCodeIncludeDetails() {
    var a = new ApiError("c", "m", Map.of("k", "v"));
    var b = new ApiError("c", "m", Map.of("k", "v"));
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertTrue(a.toString().contains("c"));
  }
}
