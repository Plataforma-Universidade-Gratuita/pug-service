package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffEntityTest {

  @Inject EntityManager em;

  private UUID newUser(String cpf, String name) {
    UUID id = UUID.randomUUID();
    em.createNativeQuery("insert into users(id,cpf,name) values (?,?,?)")
        .setParameter(1, id)
        .setParameter(2, cpf)
        .setParameter(3, name)
        .executeUpdate();
    return id;
  }

  private UUID newEntity() {
    UUID city = UUID.randomUUID();
    em.createNativeQuery("insert into cities(id,name,ibge_code) values (?,?,?)")
        .setParameter(1, city)
        .setParameter(2, "São José")
        .setParameter(3, "4216609")
        .executeUpdate();

    UUID ent = UUID.randomUUID();
    em.createNativeQuery("insert into entities(id,cnpj,name,city_id,active) values (?,?,?,?,true)")
        .setParameter(1, ent)
        .setParameter(2, "11222333000181")
        .setParameter(3, "Ent A")
        .setParameter(4, city)
        .executeUpdate();
    return ent;
  }

  @Test
  @TestTransaction
  void persistAndReadBack() {
    var userId = newUser("93541134780", "Alice");
    var entityId = newEntity();

    var e =
        StaffEntity.builder()
            .userId(userId)
            .email("person@example.com")
            .entityId(entityId)
            .active(true)
            .build();

    em.persist(e);
    em.flush();
    assertNotNull(e.getId());

    var found = em.find(StaffEntity.class, e.getId());
    assertEquals(userId, found.getUserId());
    assertEquals("person@example.com", found.getEmail());
    assertEquals(entityId, found.getEntityId());
    assertTrue(found.isActive());
  }
}
