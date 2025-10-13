package com.pug.partner.service.queries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pug.shared.infra.persistence.PageRequest;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ListEntitiesByCityQueryTest {

  @Test
  void gettersWork() {
    var q = new ListEntitiesByCityQuery(UUID.randomUUID(), new PageRequest(1, 20));
    assertNotNull(q.cityId());
    assertEquals(1, q.pageRequest().page());
    assertEquals(20, q.pageRequest().size());
  }
}
