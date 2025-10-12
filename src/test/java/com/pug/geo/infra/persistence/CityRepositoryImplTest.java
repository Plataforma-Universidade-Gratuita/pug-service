package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityRepositoryImplTest {

  @Inject CityRepository repo;
  @Inject EntityManager em;

  private static CityEntity newCity(String name, String ibge) {
    return CityEntity.builder().name(name).ibgeCode(ibge).build();
  }

  private static String uniqIbge() {
    String s = String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits()));
    return s.substring(0, 7);
  }

  @Test
  @TestTransaction
  void findByIbgeCodeReturnsSaved() {
    var c = newCity("Testópolis", uniqIbge());
    em.persist(c);
    em.flush();
    em.clear();

    var out = repo.findByIbgeCode(c.getIbgeCode());
    assertTrue(out.isPresent());
    assertEquals(c.getName(), out.get().getName());
  }

  @Test
  @TestTransaction
  void listByPatternIsAccentInsensitiveAndCaseInsensitive() {
    var name = "Florianópolis-" + UUID.randomUUID().toString().substring(0, 8);
    var c = newCity(name, uniqIbge());
    em.persist(c);
    em.flush();
    em.clear();

    Page<City> p1 = repo.listByPattern("florianopolis", new PageRequest(0, 10));
    assertTrue(p1.items().stream().anyMatch(x -> x.getId().equals(c.getId())));

    Page<City> p2 = repo.listByPattern("FLORIANOPOLIS", new PageRequest(0, 10));
    assertTrue(p2.items().stream().anyMatch(x -> x.getId().equals(c.getId())));

    Page<City> p3 = repo.listByPattern("floriano", new PageRequest(0, 10));
    assertTrue(p3.items().stream().anyMatch(x -> x.getId().equals(c.getId())));

    Page<City> none = repo.listByPattern("xyz-not-found", new PageRequest(0, 10));
    assertTrue(none.items().isEmpty());
    assertEquals(0, none.total());
  }

  @Test
  @TestTransaction
  void paginationWorks() {
    var base = "AlphaVille-";
    for (int i = 0; i < 5; i++) em.persist(newCity(base + i, uniqIbge()));
    em.flush();
    em.clear();

    var pr0 = new PageRequest(0, 2);
    var pr1 = new PageRequest(1, 2);

    Page<City> page0 = repo.listByPattern("alphaville", pr0);
    Page<City> page1 = repo.listByPattern("alphaville", pr1);

    assertEquals(2, page0.items().size());
    assertEquals(2, page1.items().size());
    assertTrue(page0.total() >= 5);

    var ids0 = page0.items().stream().map(City::getId).toList();
    var ids1 = page1.items().stream().map(City::getId).toList();
    assertTrue(ids0.stream().noneMatch(ids1::contains));
  }
}
