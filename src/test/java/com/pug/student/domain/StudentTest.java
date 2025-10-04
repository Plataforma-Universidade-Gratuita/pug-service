package com.pug.student.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class StudentTest {
  @Test
  void canInstantiate() {
    var x = new Student();
    assertNotNull(x);
  }
}
