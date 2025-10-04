package com.pug.identity.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void canInstantiate() {
    var x = new User();
    assertNotNull(x);
  }
}
