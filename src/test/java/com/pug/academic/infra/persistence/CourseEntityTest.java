package com.pug.academic.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseEntityTest {

  @Inject Validator validator;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  private static CourseEntity course(String name, SchoolEntity s) {
    return CourseEntity.builder().name(name).school(s).build();
  }

  @Test
  void beanValidation_nameAndSchool() {
    var s = school("UFSC");
    Set<ConstraintViolation<CourseEntity>> v1 = validator.validate(course(" ", s));
    assertFalse(v1.isEmpty());
    assertTrue(v1.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

    Set<ConstraintViolation<CourseEntity>> v2 = validator.validate(course("Medicine", null));
    assertFalse(v2.isEmpty());
    assertTrue(v2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("school")));
  }

  @Test
  @TestTransaction
  void persistence_generatesId_andUniqueName() {
    var sc = school("UFSC");
    em.persist(sc);

    var a = course("Law", sc);
    em.persist(a);
    em.flush();
    assertNotNull(a.getId());

    var dup = course("Law", sc);
    assertThrows(
        Exception.class,
        () -> {
          em.persist(dup);
          em.flush();
        });
  }
}
