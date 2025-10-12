package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserEntityTest {

  @Inject EntityManager em;

  private static UserEntity u(String name, String cpf) {
    return UserEntity.builder().name(name).cpf(cpf).build();
  }

  @Test
  @TestTransaction
  void persistValidAcceptsMaskedCpfAndStoresDigits() {
    var e = u("Alice", "935.411.347-80");
    em.persist(e);
    em.flush();
    em.clear();

    var found = em.find(UserEntity.class, e.getId());
    assertNotNull(found.getId());
    assertEquals("93541134780", found.getCpf());
    assertEquals("Alice", found.getName());
  }

  @Test
  @TestTransaction
  void cpfAnnotationRejectsInvalid() {
    var e = u("Bob", "11111111111");
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          em.persist(e);
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void blankNameRejected() {
    var e = u(" ", "935.411.347-80");
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          em.persist(e);
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void tooLongNameRejected() {
    var e = u("x".repeat(151), "935.411.347-80");
    assertThrows(
        ConstraintViolationException.class,
        () -> {
          em.persist(e);
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void uniqueCpfViolation() {
    var a = u("Alice", "935.411.347-80");
    em.persist(a);
    em.flush();

    var b = u("Bruna", "935.411.347-80");
    var ex =
        assertThrows(
            PersistenceException.class,
            () -> {
              em.persist(b);
              em.flush();
            });
    assertTrue(isUniqueViolation(ex));
  }

  private static boolean isUniqueViolation(Throwable t) {
    for (Throwable e = t; e != null; e = e.getCause()) {
      if (e instanceof org.hibernate.exception.ConstraintViolationException cve) {
        return "23505".equals(cve.getSQLState());
      }
      if (e instanceof org.postgresql.util.PSQLException pg) {
        return "23505".equals(pg.getSQLState());
      }
    }
    return false;
  }
}
