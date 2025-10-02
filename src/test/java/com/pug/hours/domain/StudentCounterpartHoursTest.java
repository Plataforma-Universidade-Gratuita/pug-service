package com.pug.hours.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StudentCounterpartHoursTest {
  @Test void canInstantiate() {
    var x = new StudentCounterpartHours();
    assertNotNull(x);
  }
}
