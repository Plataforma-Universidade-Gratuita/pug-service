package com.pug.geo.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CityTest {
  @Test void canInstantiate() {
    var x = new City();
    assertNotNull(x);
  }
}
