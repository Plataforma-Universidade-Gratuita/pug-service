package com.pug.identity.domain.UserTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.User;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserJpaMappingTest {

  @Inject EntityManager em;

  @Test
  @Transactional
  void persistSetsUuidv7AndTimestamps() {
    var u = User.builder().cpf("11122233344455").name("Grace Hopper").build();
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
  @Transactional
  void updateChangesUpdatedAt() throws InterruptedException {
    var u = User.builder().cpf("55544433322211").name("Alan Turing").build();
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
  @Transactional
  void cpfIsUnique() {
    var a = User.builder().cpf("00011122233344").name("A").build();
    var b = User.builder().cpf("00011122233344").name("B").build();

    em.persist(a);
    em.flush();

    em.persist(b);
    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @Transactional
  void nameMaxLengthEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                              insert into users (id, cpf, name, created_at, updated_at)
                              values (gen_random_uuid(), :cpf, :name, now(), now())
                            """)
              .setParameter("cpf", "99988877766655")
              .setParameter("name", "x".repeat(151))
              .executeUpdate();
          em.flush();
        });
  }
}
