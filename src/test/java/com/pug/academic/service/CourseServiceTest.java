package com.pug.academic.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.academic.domain.Course;
import com.pug.academic.infra.persistence.SchoolEntity;
import com.pug.shared.application.StringQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.infra.persistence.PageRequest;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseServiceTest {

  @Inject CourseService service;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  @Test
  @TestTransaction
  void getByIdNameAndListBySchool() {
    var s = school("UDESC");
    em.persist(s);

    var p = service.listBySchool(new UuidQuery(s.getId()), new PageRequest(0, 10));
    assertTrue(p.items().isEmpty());

    var c =
        com.pug.academic.infra.persistence.CourseEntity.builder().name("Math").school(s).build();
    em.persist(c);
    em.flush();
    em.clear();

    assertTrue(service.getById(new UuidQuery(c.getId())).isPresent());
    assertTrue(service.getByName(new StringQuery("math")).isPresent());

    var page = service.listBySchool(new UuidQuery(s.getId()), new PageRequest(0, 10));
    List<String> names = page.items().stream().map(Course::getName).toList();
    assertEquals(List.of("Math"), names);
  }
}
