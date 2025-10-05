package com.pug.geo.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityJpaMappingTest {

  @Inject EntityManager em;

  private static String uniq(String base) {
    return base + "-" + UUID.randomUUID().toString().substring(0, 8);
  }

  private static String ibge() {
    return String.format("%07d", ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999));
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7() {
    var c = City.builder().name(uniq("Alpha City")).ibgeCode(ibge()).build();
    em.persist(c);
    em.flush();
    em.clear();

    var found = em.find(City.class, c.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
  }

  @Test
  @TestTransaction
  void nameIsUnique() {
    var base = uniq("Fooville");
    var a = City.builder().name(base).ibgeCode(ibge()).build();
    var b = City.builder().name(base).ibgeCode(ibge()).build();

    em.persist(a);
    em.flush();

    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void ibgeCodeIsUnique() {
    var code = ibge();
    var a = City.builder().name(uniq("Bar Town")).ibgeCode(code).build();
    var b = City.builder().name(uniq("Baz Borough")).ibgeCode(code).build();

    em.persist(a);
    em.flush();

    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void nameMaxLengthEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  "insert into cities (id, name, ibge_code) values (gen_random_uuid(), :name, :ibge)")
              .setParameter("name", "x".repeat(101))
              .setParameter("ibge", ibge())
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void ibgeCodeLengthEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  "insert into cities (id, name, ibge_code) values (gen_random_uuid(), :name, :ibge)")
              .setParameter("name", uniq("Qux City"))
              .setParameter("ibge", "12345678") // invalid > 7
              .executeUpdate();
          em.flush();
        });
  }
}
