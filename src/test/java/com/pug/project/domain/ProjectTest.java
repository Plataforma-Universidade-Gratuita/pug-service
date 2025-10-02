package com.pug.project.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectTest {
  @Test void canInstantiate() {
    var x = new Project();
    assertNotNull(x);
  }
}
