package com.pug.shared.domain.id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UuidV7HibernateTest {

  @Test
  void generatesUuidV7() {
    var gen = new UuidV7Hibernate();
    UUID u = gen.generateUuid(null);
    assertNotNull(u);
    assertEquals(7, u.version());
    assertEquals(2, u.variant());
  }
}
