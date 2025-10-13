package com.pug.partner.infra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.partner.domain.Staff;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class StaffMapperTest {

  @Test
  void mapsBothWays() {
    var id = UUID.randomUUID();
    var user = UUID.randomUUID();
    var ent = UUID.randomUUID();

    var d = Staff.newActive().id(id).userId(user).email("x@y.com").entityId(ent).build();
    var e = StaffMapper.toEntity(d);
    assertEquals(id, e.getId());
    assertEquals(user, e.getUserId());
    assertEquals("x@y.com", e.getEmail());
    assertEquals(ent, e.getEntityId());
    assertTrue(e.isActive());

    var d2 = StaffMapper.toDomain(e);
    assertEquals(d, d2);
  }
}
