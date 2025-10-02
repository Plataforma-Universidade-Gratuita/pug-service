package com.pug.student.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StudentTest {
  @Test void canInstantiate() {
    var x = new Student();
    assertNotNull(x);
  }
}
