package com.pug.hours.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StudentCounterpartHoursTest {
  @Test
  void canInstantiate() {
    var x = new StudentCounterpartHours();
    assertNotNull(x);
  }
}
