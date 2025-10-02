package com.pug.attendance.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectAttendanceTest {
  @Test void canInstantiate() {
    var x = new ProjectAttendance();
    assertNotNull(x);
  }
}
