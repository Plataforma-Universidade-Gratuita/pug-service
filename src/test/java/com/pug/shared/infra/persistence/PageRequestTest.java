package com.pug.shared.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageRequestTest {

  @Test
  void ctorValidatesBounds() {
    assertThrows(IllegalArgumentException.class, () -> new PageRequest(-1, 10));
    assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 0));
    assertThrows(IllegalArgumentException.class, () -> new PageRequest(0, 1001));

    var pr = new PageRequest(3, 50);
    assertEquals(150, pr.offset());
  }
}
