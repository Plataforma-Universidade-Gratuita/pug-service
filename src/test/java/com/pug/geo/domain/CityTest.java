package com.pug.geo.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CityTest {
  @Test
  void canInstantiate() {
    var x = new City();
    assertNotNull(x);
  }
}
