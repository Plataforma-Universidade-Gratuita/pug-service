package com.pug.partner.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import com.pug.partner.domain.PartnerEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PartnerEntityJpaMappingTest {

  @Inject EntityManager em;

  private static final String VALID_CNPJ = "11222333000181"; // valid checksum

  private City newCity(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
    em.persist(c);
    return c;
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7AndTimestamps() {
    var city = newCity("Florianópolis2", "4205401");
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("Org A").city(city).build();

    em.persist(e);
    em.flush();
    em.clear();

    var found = em.find(PartnerEntity.class, e.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
  }

  @Test
  @TestTransaction
  void updateChangesUpdatedAt() throws InterruptedException {
    var city = newCity("Blumenau2", "4202402");
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("Org B").city(city).build();
    em.persist(e);
    em.flush();

    var beforeUpdatedAt = e.getUpdatedAt();
    var beforeName = e.getName();

    Thread.sleep(5);
    e.setName("Org B Updated");
    em.flush();

    assertEquals("Org B", beforeName);
    assertEquals("Org B Updated", e.getName());
    assertTrue(e.getUpdatedAt().isAfter(beforeUpdatedAt));
  }

  @Test
  @TestTransaction
  void cnpjIsUnique() {
    var city = newCity("Joinville2", "4209103");
    var a = PartnerEntity.builder().cnpj(VALID_CNPJ).name("A").city(city).build();
    var b = PartnerEntity.builder().cnpj(VALID_CNPJ).name("B").city(city).build();

    em.persist(a);
    em.flush();

    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void nameMaxLengthEnforcedByDb() {
    var city = newCity("Lages", "4209300");

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into entities (id, cnpj, name, city_id, active, created_at, updated_at)
                                            values (gen_random_uuid(), :cnpj, :name, :city, true, now(), now())
                                            """)
              .setParameter("cnpj", "11222333000181")
              .setParameter("name", "x".repeat(151))
              .setParameter("city", city.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void addressMaxLengthEnforcedByDb() {
    var city = newCity("Criciúma", "4204608");

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into entities (id, cnpj, name, city_id, address, active, created_at, updated_at)
                                            values (gen_random_uuid(), :cnpj, :name, :city, :addr, true, now(), now())
                                            """)
              .setParameter("cnpj", "11222333000181")
              .setParameter("name", "Org C")
              .setParameter("city", city.getId())
              .setParameter("addr", "y".repeat(255))
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void cnpjMaskedInputPersistsAsDigitsOnly() {
    var city = newCity("Itajaí", "4208204");
    var e =
        PartnerEntity.builder()
            .cnpj("11.222.333/0001-81") // masked
            .name("Org D")
            .city(city)
            .build();

    em.persist(e);
    em.flush();
    em.clear();

    var reloaded = em.find(PartnerEntity.class, e.getId());
    assertEquals("11222333000181", reloaded.getCnpj());

    var raw =
        (String)
            em.createNativeQuery("select cnpj from entities where id = :id")
                .setParameter("id", e.getId())
                .getSingleResult();
    assertEquals("11222333000181", raw);
    assertEquals(14, raw.length());
  }

  @Test
  @TestTransaction
  void cnpjLengthEnforcedByDbColumn() {
    var city = newCity("Chapecó", "4204202");

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into entities (id, cnpj, name, city_id, active, created_at, updated_at)
                                            values (gen_random_uuid(), :cnpj, :name, :city, true, now(), now())
                                            """)
              .setParameter("cnpj", "123456789012345") // 15 chars
              .setParameter("name", "Org E")
              .setParameter("city", city.getId())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void activeDefaultsToTrue() {
    var city = newCity("Tubarão", "4218707");
    var e = PartnerEntity.builder().cnpj(VALID_CNPJ).name("Org F").city(city).build();
    em.persist(e);
    em.flush();
    em.clear();

    var found = em.find(PartnerEntity.class, e.getId());
    assertTrue(found.isActive());
  }
}
