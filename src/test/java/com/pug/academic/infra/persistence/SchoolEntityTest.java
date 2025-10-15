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
class SchoolEntityTest {

  @Inject Validator validator;
  @Inject EntityManager em;

  private static SchoolEntity school(String name) {
    return SchoolEntity.builder().name(name).build();
  }

  @Test
  void beanValidation_nameRequiredAndMaxLen() {
    var blank = school(" ");
    Set<ConstraintViolation<SchoolEntity>> v1 = validator.validate(blank);
    assertFalse(v1.isEmpty());
    assertTrue(v1.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));

    var longName = school("X".repeat(101));
    Set<ConstraintViolation<SchoolEntity>> v2 = validator.validate(longName);
    assertFalse(v2.isEmpty());
    assertTrue(v2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  @TestTransaction
  void persistence_generatesId_andUniqueNameConstraint() {
    var a = school("UFSC");
    em.persist(a);
    em.flush();
    assertNotNull(a.getId());

    var dup = school("UFSC");
    assertThrows(
        Exception.class,
        () -> {
          em.persist(dup);
          em.flush();
        });
  }
}
