package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.FieldOfStudy;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FieldOfStudyRepositoryTest {

  @Inject FieldOfStudyRepository repo;
  @Inject EntityManager em;

  @Test
  @TestTransaction
  void existsByName_trueWhenPresent_falseWhenAbsent() {
    persist("law");

    assertTrue(repo.existsByName("law"));
    assertFalse(repo.existsByName("medicine"));
  }

  @Test
  @TestTransaction
  void existsByNameForAnother_ignoresSame_checksOthers() {
    var a = persist("engineering");
    var b = persist("physics");

    assertFalse(repo.existsByNameForAnother("engineering", a.getId()));
    assertTrue(repo.existsByNameForAnother("engineering", b.getId()));
  }

  @Test
  @TestTransaction
  void listAllSorted_ordersAscendingByName() {
    persist("zeta");
    persist("alpha");
    persist("beta");

    var out = repo.listAllSorted();
    List<String> names = out.stream().map(FieldOfStudy::getName).toList();

    assertEquals(List.of("alpha", "beta", "zeta"), names);
  }

  @Test
  @TestTransaction
  void listByPattern_matchesAccentAndCaseInsensitive_andHonorsLimit() {
    persist("engenharia elétrica");
    persist("engenharia civil");
    persist("direito");

    var e1 = repo.listByPattern("eletrica", 10);
    assertTrue(e1.stream().anyMatch(x -> x.getName().equals("engenharia elétrica")));

    var e2 = repo.listByPattern("ENGENHARIA", 10);
    assertTrue(e2.stream().anyMatch(x -> x.getName().equals("engenharia elétrica")));
    assertTrue(e2.stream().anyMatch(x -> x.getName().equals("engenharia civil")));

    var limited = repo.listByPattern("engenharia", 1);
    assertEquals(1, limited.size());

    var none = repo.listByPattern("xyznotfound", 5);
    assertTrue(none.isEmpty());
  }

  private FieldOfStudy persist(String name) {
    var e = FieldOfStudy.builder().name(name).build();
    em.persist(e);
    em.flush();
    return e;
  }
}
