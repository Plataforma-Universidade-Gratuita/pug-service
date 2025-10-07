package com.pug.academic.domain.fieldOfStudy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pug.academic.domain.FieldOfStudy;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FieldOfStudyJpaMappingTest {

  @Inject EntityManager em;

  @Test
  @TestTransaction
  void persistAndLoadOk() {
    var e = FieldOfStudy.builder().name("Health Sciences").build();
    em.persist(e);
    em.flush();
    em.clear();

    var found = em.find(FieldOfStudy.class, e.getId());
    assertNotNull(found);
    assertEquals("Health Sciences", found.getName());
  }

  @Test
  @TestTransaction
  void uniqueNameEnforcedByDatabase() {
    var a = FieldOfStudy.builder().name("Law").build();
    em.persist(a);
    em.flush();

    var b = FieldOfStudy.builder().name("Law").build();
    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }
}
