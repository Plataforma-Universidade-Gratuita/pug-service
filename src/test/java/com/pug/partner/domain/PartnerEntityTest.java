package com.pug.partner.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PartnerEntityTest {
  @Test void canInstantiate() {
    var x = new PartnerEntity();
    assertNotNull(x);
  }
}
