package com.pug.identity.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {
  @Test void canInstantiate() {
    var x = new User();
    assertNotNull(x);
  }
}
