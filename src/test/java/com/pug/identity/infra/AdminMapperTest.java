package com.pug.identity.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.pug.identity.infra.persistence.AdminEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AdminMapperTest {
  @Test
  void toDomainCopiesFields() {
    var id = UUID.randomUUID();
    var granted = Instant.parse("2025-01-01T12:00:00Z");
    var e = new AdminEntity(id);
    e.setGrantedAt(granted);

    var d = AdminMapper.toDomain(e);
    assertNotNull(d);
    assertEquals(id, d.userId());
    assertEquals(granted, d.grantedAt());
  }

  @Test
  void toDomainNullSafe() {
    assertNull(AdminMapper.toDomain(null));
  }
}
