package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SchoolRepositoryImplTest {

  @Inject SchoolRepository repo;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  @Test
  @TestTransaction
  void saveFindAndUpdate() {
    var saved = repo.save(School.builder().name("UFSC").build());
    assertNotNull(saved.getId());
    assertEquals("UFSC", saved.getName());

    var byId = repo.findOptionalById(saved.getId());
    assertTrue(byId.isPresent());

    var byName = repo.findByNameIgnoreCase("ufsc");
    assertTrue(byName.isPresent());
    assertEquals(saved.getId(), byName.get().getId());

    var updated = repo.save(saved.toBuilder().name("UDESC").build());
    assertEquals("UDESC", updated.getName());
    assertEquals(saved.getId(), updated.getId());
  }

  @Test
  @TestTransaction
  void listOrderedAndPagination() {
    em.persist(school("Zeta"));
    em.persist(school("Alpha"));
    em.persist(school("Beta"));
    em.flush();
    em.clear();

    Page<School> p1 = repo.listOrdered(new PageRequest(0, 2));
    assertEquals(2, p1.items().size());
    List<String> names1 = p1.items().stream().map(School::getName).toList();
    assertEquals(List.of("Alpha", "Beta"), names1);

    Page<School> p2 = repo.listOrdered(new PageRequest(5, 1));
    List<String> names2 = p2.items().stream().map(School::getName).toList();
    assertEquals(List.of("Zeta"), names2);
  }
}
