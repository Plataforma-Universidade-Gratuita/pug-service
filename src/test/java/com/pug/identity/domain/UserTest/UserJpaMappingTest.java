package com.pug.identity.domain.UserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.User;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserJpaMappingTest {

  @Inject EntityManager em;

  private final String VALID_CPF = "93541134780";

  @Test
  @TestTransaction
  void persistSetsUuidv7AndTimestamps() {
    var u = User.builder().cpf(VALID_CPF).name("Grace Hopper").build();
    em.persist(u);
    em.flush();
    em.clear();

    var found = em.find(User.class, u.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
  }

  @Test
  @TestTransaction
  void updateChangesUpdatedAt() throws InterruptedException {
    var u = User.builder().cpf(VALID_CPF).name("Alan Turing").build();
    em.persist(u);
    em.flush();
    var beforeUpdatedAt = u.getUpdatedAt();
    var beforeName = u.getName();

    Thread.sleep(5);
    u.setName("Alan M. Turing");
    em.flush();

    assertEquals("Alan Turing", beforeName);
    assertEquals("Alan M. Turing", u.getName());
    assertTrue(u.getUpdatedAt().isAfter(beforeUpdatedAt));
  }

  @Test
  @TestTransaction
  void cpfIsUnique() {
    var a = User.builder().cpf(VALID_CPF).name("A").build();
    var b = User.builder().cpf(VALID_CPF).name("B").build();

    em.persist(a);
    em.flush();

    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void nameMaxLengthEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into users (id, cpf, name, created_at, updated_at)
                                            values (gen_random_uuid(), :cpf, :name, now(), now())
                                            """)
              .setParameter("cpf", "12345678901")
              .setParameter("name", "x".repeat(151))
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void cpfMaskedInputPersistsAsDigitsOnly() {
    var u = User.builder().cpf("935.411.347-80").name("Ada").build(); // masked
    em.persist(u);
    em.flush();
    em.clear();

    var reloaded = em.find(User.class, u.getId());
    assertEquals("93541134780", reloaded.getCpf());

    var raw =
        (String)
            em.createNativeQuery("select cpf from users where id = :id")
                .setParameter("id", u.getId())
                .getSingleResult();
    assertEquals("93541134780", raw);
    assertEquals(11, raw.length());
  }

  @Test
  @TestTransaction
  void cpfLengthEnforcedByDbColumn() {
    assertThrows(
        jakarta.persistence.PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                              insert into users (id, cpf, name, created_at, updated_at)
                              values (gen_random_uuid(), :cpf, :name, now(), now())
                            """)
              .setParameter("cpf", "123456789012") // 12 chars
              .setParameter("name", "Ada")
              .executeUpdate();
          em.flush();
        });
  }
}
