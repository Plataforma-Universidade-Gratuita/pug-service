package com.pug.partner.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PartnerEntityTest {
  @Test
  void canInstantiate() {
    var x = new PartnerEntity();
    assertNotNull(x);
  }
}
