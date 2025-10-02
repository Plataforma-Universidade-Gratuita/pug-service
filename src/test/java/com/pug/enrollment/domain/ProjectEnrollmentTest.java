package com.pug.enrollment.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectEnrollmentTest {
  @Test void canInstantiate() {
    var x = new ProjectEnrollment();
    assertNotNull(x);
  }
}
