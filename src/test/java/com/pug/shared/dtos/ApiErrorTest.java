package com.pug.shared.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ApiErrorTest {

  @Test
  void nullDetailsBecomesEmptyAndAccessorReturnsDefensiveCopy() {
    var err = new ApiError("CODE", "msg", null);
    assertNotNull(err.details());
    assertTrue(err.details().isEmpty());

    var copy = err.details();
    copy.put("k", "v");
    assertTrue(err.details().isEmpty());
  }

  @Test
  void ctorDefensiveCopyPreventsExternalMutationAndPreservesOrder() {
    var in = new LinkedHashMap<String, Object>();
    in.put("a", 1);
    in.put("b", 2);

    var err = new ApiError("CODE", "msg", in);

    in.put("c", 3);

    var d1 = err.details();
    assertEquals(2, d1.size());
    assertEquals(List.of("a", "b"), d1.keySet().stream().toList());

    d1.put("x", 9);
    var d2 = err.details();
    assertEquals(2, d2.size());
    assertFalse(d2.containsKey("x"));
  }

  @Test
  void factoryOfBuildsSameAsConstructor() {
    var map = Map.of("k", (Object) "v");
    var a = new ApiError("C", "m", map);
    var b = ApiError.of("C", "m", map);

    assertEquals(a.code(), b.code());
    assertEquals(a.message(), b.message());
    assertEquals(a.details(), b.details());
  }
}
