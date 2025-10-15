package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseRepositoryImplTest {

  @Inject CourseRepository repo;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  @Test
  @TestTransaction
  void saveFindUpdateAndListBySchool() {
    var s = school("UFSC");
    em.persist(s);

    var saved = repo.save(Course.builder().name("History").schoolId(s.getId()).build());
    assertNotNull(saved.getId());

    var byId = repo.findOptionalById(saved.getId());
    assertTrue(byId.isPresent());

    var byName = repo.findByNameIgnoreCase("history");
    assertTrue(byName.isPresent());

    var updated = repo.save(saved.toBuilder().name("Geography").build());
    assertEquals("Geography", updated.getName());

    repo.save(Course.builder().name("Arts").schoolId(s.getId()).build());
    repo.save(Course.builder().name("Biology").schoolId(s.getId()).build());

    var page1 = repo.listBySchool(s.getId(), new PageRequest(0, 2));
    List<String> names1 = page1.items().stream().map(Course::getName).toList();
    assertEquals(List.of("Arts", "Biology"), names1);

    var page2 = repo.listBySchool(s.getId(), new PageRequest(1, 2));
    List<String> names2 = page2.items().stream().map(Course::getName).toList();
    assertEquals(List.of("Geography"), names2);
  }
}
