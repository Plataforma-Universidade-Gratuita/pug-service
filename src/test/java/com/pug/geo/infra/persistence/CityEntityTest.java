package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityEntityTest {

  @Inject EntityManager em;

  private static CityEntity city(String name, String ibge) {
    return CityEntity.builder().name(name).ibgeCode(ibge).build();
  }

  @Test
  @TestTransaction
  void persistsWithUuidV7Id() {
    var c = city("Palhoça-" + UUID.randomUUID(), "4211900");
    em.persist(c);
    em.flush();
    assertNotNull(c.getId());
    assertEquals(7, c.getId().version());
  }

  @Test
  @TestTransaction
  void beanValidationRejectsBlankFields() {
    var c1 = city("   ", "4211900");
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          em.persist(c1);
          em.flush();
        });

    var c2 = city("Palhoça", "   ");
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          em.persist(c2);
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void uniqueNameIsEnforced() {
    String name = "Uniq-" + UUID.randomUUID();
    em.persist(city(name, "4216602"));
    em.flush();

    var dup = city(name, "4205407");
    PersistenceException ex =
        assertThrows(
            PersistenceException.class,
            () -> {
              em.persist(dup);
              em.flush();
            });
    assertTrue(ex.getMessage().toLowerCase().contains("unique") || ex.getCause() != null);
  }

  @Test
  @TestTransaction
  void uniqueIbgeCodeIsEnforced() {
    String ibge = "4205406";
    em.persist(city("City-" + UUID.randomUUID(), ibge));
    em.flush();

    var dup = city("Another-" + UUID.randomUUID(), ibge);
    PersistenceException ex =
        assertThrows(
            PersistenceException.class,
            () -> {
              em.persist(dup);
              em.flush();
            });
    assertTrue(ex.getMessage().toLowerCase().contains("unique") || ex.getCause() != null);
  }
}
