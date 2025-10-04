package com.pug.attendance.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProjectAttendanceTest {
  @Test
  void canInstantiate() {
    var x = new ProjectAttendance();
    assertNotNull(x);
  }
}
