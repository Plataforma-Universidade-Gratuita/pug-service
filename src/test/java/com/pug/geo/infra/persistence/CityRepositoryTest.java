package com.pug.geo.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.geo.domain.City;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityRepositoryTest {

  @Inject CityRepository repo;
  @Inject EntityManager em;

  private static String uniq(String base) {
    return base + "-" + UUID.randomUUID().toString().substring(0, 8);
  }

  private static String ibge(int seed) {
    return String.format("%07d", 1000000 + (Math.abs(seed) % 8999999));
  }

  private City persist(String name, String ibge) {
    var c = City.builder().name(name).ibgeCode(ibge).build();
    em.persist(c);
    return c;
  }

  @Test
  @TestTransaction
  void listAllSortedReturnsAscendingByName() {
    var a = persist(uniq("Alpha"), ibge(1));
    var c = persist(uniq("Charlie"), ibge(2));
    var b = persist(uniq("Bravo"), ibge(3));
    em.flush();
    em.clear();

    List<City> out = repo.listAllSorted();
    var names = out.stream().map(City::getName).toList();
    int ia = names.indexOf(a.getName());
    int ib = names.indexOf(b.getName());
    int ic = names.indexOf(c.getName());
    assertTrue(ia < ib && ib < ic);
  }

  @Test
  @TestTransaction
  void listByPatternIsCaseInsensitiveAndHonorsLimit() {
    var base = uniq("Fooville");
    persist(base + " North", ibge(11));
    persist(base + " South", ibge(12));
    persist(base + " East", ibge(13));
    em.flush();
    em.clear();

    var out2 = repo.listByPattern(base.substring(0, 3).toUpperCase(), 2);
    assertEquals(2, out2.size());

    var outAll = repo.listByPattern(base.toLowerCase(), 10);
    assertTrue(outAll.size() >= 3);
    assertTrue(
        outAll.stream().allMatch(c -> c.getName().toLowerCase().contains(base.toLowerCase())));
  }

  @Test
  @TestTransaction
  void findByIbgeCodeFoundAndEmpty() {
    var name = uniq("Bar Town");
    var code = ibge(21);
    persist(name, code);
    em.flush();
    em.clear();

    var found = repo.findByIbgeCode(code);
    assertTrue(found.isPresent());
    assertEquals(name, found.get().getName());

    assertTrue(repo.findByIbgeCode("9999999").isEmpty());
  }

  @Test
  @TestTransaction
  void listByPatternNoMatchesReturnsEmpty() {
    var out = repo.listByPattern("___nope___", 5);
    assertTrue(out.isEmpty());
  }

  @Test
  @TestTransaction
  void listByPatternMatchesAccentInsensitive() {
    var name = uniq("Florianópolis-");
    var c = City.builder().name(name).ibgeCode("1234567").build();
    em.persist(c);
    em.flush();
    em.clear();

    var out = repo.listByPattern("florianopolis", 10);
    assertTrue(out.stream().anyMatch(x -> x.getId().equals(c.getId())));

    var out2 = repo.listByPattern("floriano", 10);
    assertTrue(out2.stream().anyMatch(x -> x.getId().equals(c.getId())));

    var out3 = repo.listByPattern("FLORIANOPOLIS", 10);
    assertTrue(out3.stream().anyMatch(x -> x.getId().equals(c.getId())));

    var none = repo.listByPattern("xyznotfound", 10);
    assertTrue(none.isEmpty());
  }
}
