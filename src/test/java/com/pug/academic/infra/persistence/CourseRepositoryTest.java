package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseRepositoryTest {

  @Inject CourseRepository repo;
  @Inject EntityManager em;

  @Test
  @TestTransaction
  void existsByNameTrueWhenPresentFalseWhenAbsent() {
    var f = persistField("engineering");
    persistCourse("databases", f);

    assertTrue(repo.existsByName("databases"));
    assertFalse(repo.existsByName("networks"));
  }

  @Test
  @TestTransaction
  void existsByNameForAnotherIgnoresSameChecksOthers() {
    var f = persistField("computing");
    var a = persistCourse("algorithms", f);
    var b = persistCourse("operating systems", f);

    assertFalse(repo.existsByNameForAnother("algorithms", a.getId()));
    assertTrue(repo.existsByNameForAnother("algorithms", b.getId()));
  }

  @Test
  @TestTransaction
  void listAllSortedOrdersAscendingByName() {
    var f = persistField("law");
    persistCourse("zoology", f);
    persistCourse("algebra", f);
    persistCourse("botany", f);

    var out = repo.listAllSorted();
    List<String> names = out.stream().map(Course::getName).toList();

    assertEquals(List.of("algebra", "botany", "zoology"), names);
  }

  @Test
  @TestTransaction
  void listByPatternMatchesAccentAndCaseInsensitiveAndHonorsLimit() {
    var f = persistField("engineering");
    persistCourse("engenharia elétrica", f);
    persistCourse("engenharia civil", f);
    persistCourse("direito", f);

    var e1 = repo.listByPattern("eletrica", 10, 0);
    assertTrue(e1.stream().anyMatch(x -> x.getName().equals("engenharia elétrica")));

    var e2 = repo.listByPattern("ENGENHARIA", 10, 0);
    assertTrue(e2.stream().anyMatch(x -> x.getName().equals("engenharia elétrica")));
    assertTrue(e2.stream().anyMatch(x -> x.getName().equals("engenharia civil")));

    var limited = repo.listByPattern("engenharia", 1, 0);
    assertEquals(1, limited.size());

    var none = repo.listByPattern("xyznotfound", 5, 0);
    assertTrue(none.isEmpty());
  }

  private FieldOfStudy persistField(String name) {
    var f = FieldOfStudy.builder().name(name).build();
    em.persist(f);
    em.flush();
    return f;
  }

  private Course persistCourse(String name, FieldOfStudy field) {
    var c = Course.builder().name(name).field(field).build();
    em.persist(c);
    em.flush();
    return c;
  }
}
