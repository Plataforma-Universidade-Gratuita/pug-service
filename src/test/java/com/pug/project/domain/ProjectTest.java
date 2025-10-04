package com.pug.project.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ProjectTest {
  @Test
  void canInstantiate() {
    var x = new Project();
    assertNotNull(x);
  }
}
