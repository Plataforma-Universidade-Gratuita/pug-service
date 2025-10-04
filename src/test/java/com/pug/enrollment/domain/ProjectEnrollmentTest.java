package com.pug.enrollment.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProjectEnrollmentTest {
  @Test
  void canInstantiate() {
    var x = new ProjectEnrollment();
    assertNotNull(x);
  }
}
