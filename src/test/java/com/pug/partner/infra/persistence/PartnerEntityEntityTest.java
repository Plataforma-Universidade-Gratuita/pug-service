package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.partner.domain.Cnpj;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PartnerEntityEntityTest {

  @Inject EntityManager em;

  private UUID newCity(String name, String ibge) {
    UUID id = UUID.randomUUID();
    em.createNativeQuery("insert into cities(id,name,ibge_code) values (?,?,?)")
        .setParameter(1, id)
        .setParameter(2, name)
        .setParameter(3, ibge)
        .executeUpdate();
    return id;
  }

  @Test
  @TestTransaction
  void persistAndReadBack() {
    var cityId = newCity("Palhoça", "4211900");

    var e =
        PartnerEntityEntity.builder()
            .cnpj(Cnpj.of("19131243000197"))
            .name("Org Teste")
            .cityId(cityId)
            .address("Rua Y, 123")
            .active(true)
            .build();

    em.persist(e);
    em.flush();
    assertNotNull(e.getId());

    var found = em.find(PartnerEntityEntity.class, e.getId());
    assertEquals(Cnpj.of("19.131.243/0001-97"), found.getCnpj());
    assertEquals("Org Teste", found.getName());
    assertEquals(cityId, found.getCityId());
    assertEquals("Rua Y, 123", found.getAddress());
    assertTrue(found.isActive());
  }
}
