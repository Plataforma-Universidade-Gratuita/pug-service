package com.pug.partner.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PartnerEntityRepositoryTest {

  @Inject PartnerEntityRepository repo;
  @Inject EntityManager em;

  private City newCity(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
    em.persist(c);
    return c;
  }

  private PartnerEntity newEntity(City city, String cnpjDigits, String name) {
    var e = PartnerEntity.builder().cnpj(cnpjDigits).name(name).city(city).build();
    em.persist(e);
    return e;
  }

  @Test
  @TestTransaction
  void existsByCnpjAndFindByCnpjWork() {
    var city = newCity("Florianópolis2", "2205407");
    newEntity(city, "11222333000181", "Org A");
    em.flush();
    em.clear();

    assertTrue(repo.existsByCnpj("11222333000181"));
    assertFalse(repo.existsByCnpj("19131243000197"));

    var found = repo.findByCnpj("11222333000181");
    assertTrue(found.isPresent());
    assertEquals("11222333000181", found.get().getCnpj());

    assertTrue(repo.findByCnpj("19131243000197").isEmpty());
  }

  @Test
  @TestTransaction
  void existsByCnpjForAnotherExcludesGivenId() {
    var city = newCity("Joinville2", "4209122");
    var a = newEntity(city, "11222333000181", "Org A");
    var b = newEntity(city, "19131243000197", "Org B");
    em.flush();
    em.clear();

    assertTrue(repo.existsByCnpjForAnother("11222333000181", b.getId()));
    assertFalse(repo.existsByCnpjForAnother("11222333000181", a.getId()));
    assertFalse(repo.existsByCnpjForAnother("00987654000100", a.getId()));
  }

  @Test
  @TestTransaction
  void negativeWhenNoData() {
    assertFalse(repo.existsByCnpj("11222333000181"));
    assertTrue(repo.findByCnpj("11222333000181").isEmpty());
    assertFalse(repo.existsByCnpjForAnother("11222333000181", UUID.randomUUID()));
  }
}
