package com.pug.identity.domain.RoleTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RoleJpaMappingTest {

  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "98765432100";

  private User newUser(String name, String cpf) {
    var u = User.builder().cpf(cpf).name(name).build();
    em.persist(u);
    return u;
  }

  @Test
  @TestTransaction
  void persistSetsUuidv7TimestampsAndDefaultActive() {
    var u = newUser("Grace Hopper", CPF_A);

    var a = Role.builder().user(u).role(UserRole.ADMIN).email("admin1@example.org").build();

    em.persist(a);
    em.flush();
    em.clear();

    var found = em.find(Role.class, a.getId());
    assertNotNull(found.getId());
    assertEquals(7, found.getId().version());
    assertTrue(found.isActive());
    assertNotNull(found.getCreatedAt());
    assertNotNull(found.getUpdatedAt());
    assertFalse(found.getUpdatedAt().isBefore(found.getCreatedAt()));
  }

  @Test
  @TestTransaction
  void updateChangesUpdatedAt() throws InterruptedException {
    var u = newUser("Alan Turing", CPF_A);
    var a = Role.builder().user(u).role(UserRole.PARTNER).email("partner1@example.org").build();

    em.persist(a);
    em.flush();
    var beforeUpdatedAt = a.getUpdatedAt();
    var beforeActive = a.isActive();

    Thread.sleep(5);
    a.setActive(false);
    em.flush();

    assertTrue(beforeActive);
    assertFalse(a.isActive());
    assertTrue(a.getUpdatedAt().isAfter(beforeUpdatedAt));
  }

  @Test
  @TestTransaction
  void emailIsUnique() {
    var u1 = newUser("Ada", CPF_A);
    var u2 = newUser("Babbage", CPF_B);

    var a1 = Role.builder().user(u1).role(UserRole.ADMIN).email("dup@example.org").build();
    em.persist(a1);
    em.flush();

    var a2 = Role.builder().user(u2).role(UserRole.PARTNER).email("dup@example.org").build();
    em.persist(a2);

    assertThrows(PersistenceException.class, em::flush);
  }

  @Test
  @TestTransaction
  void emailMaxLengthEnforcedByDb() {
    // 255+ chars to break varchar(254)
    var local = "a".repeat(245); // 245 + 12 = 257
    var tooLong = local + "@example.org";

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into users_roles (id, user_id, role, email, active, created_at, updated_at)
                                            values (gen_random_uuid(), :user_id, :role, :email, true, now(), now())
                                            """)
              .setParameter("user_id", newUser("MaxLen", CPF_A).getId())
              .setParameter("role", "ADMIN")
              .setParameter("email", tooLong)
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void userForeignKeyEnforcedByDb() {
    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into users_roles (id, user_id, role, email, active, created_at, updated_at)
                                            values (gen_random_uuid(), :user_id, :role, :email, true, now(), now())
                                            """)
              // random UUID not present in users table
              .setParameter("user_id", java.util.UUID.randomUUID())
              .setParameter("role", "ADMIN")
              .setParameter("email", "fkcheck@example.org")
              .executeUpdate();
          em.flush();
        });
  }

  @Test
  @TestTransaction
  void roleNotNullEnforcedByDb() {
    var u = newUser("NullRole", CPF_A);

    assertThrows(
        PersistenceException.class,
        () -> {
          em.createNativeQuery(
                  """
                                            insert into users_roles (id, user_id, role, email, active, created_at, updated_at)
                                            values (gen_random_uuid(), :user_id, NULL, :email, true, now(), now())
                                            """)
              .setParameter("user_id", u.getId())
              .setParameter("email", "nullrole@example.org")
              .executeUpdate();
          em.flush();
        });
  }
}
