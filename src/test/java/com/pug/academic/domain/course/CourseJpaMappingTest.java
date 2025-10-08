package com.pug.academic.domain.course;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.FieldOfStudy;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CourseJpaMappingTest {

  @Inject EntityManager em;

  @Test
  @TestTransaction
  void persistGeneratesUuidV7AndTimestampsAndLoadsBack() {
    var field = persistField("engineering");

    var course = Course.builder().name("software engineering").field(field).build();
    em.persist(course);
    em.flush();
    em.clear();

    UUID id = course.getId();
    assertNotNull(id);
    assertEquals(7, id.version());

    var found = em.find(Course.class, id);
    assertNotNull(found);
    assertEquals("software engineering", found.getName());
    assertNotNull(found.getCreatedAt());
    assertEquals(found.getCreatedAt(), found.getUpdatedAt());

    found.setName("software engineering ii");
    em.flush();
    Instant updated = found.getUpdatedAt();
    assertNotNull(updated);
    assertFalse(updated.isBefore(found.getCreatedAt()));
  }

  @Test
  @TestTransaction
  void uniqueNameIsEnforced() {
    var field = persistField("law");
    var a = Course.builder().name("civil law i").field(field).build();
    em.persist(a);
    em.flush();

    var b = Course.builder().name("civil law i").field(field).build();
    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void nullFieldFailsValidationOnFlush() {
    var c = Course.builder().name("networks").field(null).build();
    em.persist(c);
    assertThrows(ConstraintViolationException.class, em::flush);
  }

  @Test
  @TestTransaction
  void blankNameFailsValidationOnFlush() {
    var field = persistField("computing");
    var c = Course.builder().name(" ").field(field).build();
    em.persist(c);
    assertThrows(ConstraintViolationException.class, em::flush);
  }

  private FieldOfStudy persistField(String name) {
    var f = FieldOfStudy.builder().name(name).build();
    em.persist(f);
    em.flush();
    return f;
  }
}
