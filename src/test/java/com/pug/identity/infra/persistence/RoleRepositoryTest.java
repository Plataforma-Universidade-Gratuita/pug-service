package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Role;
import com.pug.identity.domain.User;
import com.pug.identity.domain.enums.UserRole;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RoleRepositoryTest {

  @Inject RoleRepository repo;
  @Inject EntityManager em;

  private static final String CPF_A = "93541134780";
  private static final String CPF_B = "98765432100";

  private User newUser(String name, String cpf) {
    var u = User.builder().name(name).cpf(cpf).build();
    em.persist(u);
    return u;
  }

  private Role newRole(User u, String email, UserRole r) {
    var role = Role.builder().user(u).email(email).role(r).build();
    em.persist(role);
    return role;
  }

  @Test
  @TestTransaction
  void existsByEmail_and_findByEmail_work() {
    var u = newUser("Ada", CPF_A);
    newRole(u, "admin@example.org", UserRole.ADMIN);
    em.flush();
    em.clear();

    assertTrue(repo.existsByEmail("admin@example.org"));
    assertFalse(repo.existsByEmail("missing@example.org"));

    var found = repo.findByEmail("admin@example.org");
    assertTrue(found.isPresent());
    assertEquals("admin@example.org", found.get().getEmail());

    assertTrue(repo.findByEmail("missing@example.org").isEmpty());
  }

  @Test
  @TestTransaction
  void existsFormerStudentByUserId_true_only_when_user_has_fs() {
    var u1 = newUser("Bob", CPF_A);
    var u2 = newUser("Carol", CPF_B);

    newRole(u1, "fs1@example.org", UserRole.FORMER_STUDENT);
    newRole(u2, "admin2@example.org", UserRole.ADMIN);
    em.flush();
    em.clear();

    assertTrue(repo.existsFormerStudentByUserId(u1.getId()));
    assertFalse(repo.existsFormerStudentByUserId(u2.getId()));
  }

  @Test
  @TestTransaction
  void existsByEmailForAnother_excludes_given_id() {
    var u = newUser("Dave", CPF_A);
    var r1 = newRole(u, "dup@example.org", UserRole.ADMIN);
    var r2 = newRole(u, "other@example.org", UserRole.PARTNER);
    em.flush();
    em.clear();

    assertTrue(repo.existsByEmailForAnother("dup@example.org", r2.getId()));
    assertFalse(repo.existsByEmailForAnother("dup@example.org", r1.getId()));
    assertFalse(repo.existsByEmailForAnother("nope@example.org", r1.getId()));
  }

  @Test
  @TestTransaction
  void existsFormerStudentForAnother_excludes_given_role_id() {
    var u = newUser("Eve", CPF_A);
    var fs = newRole(u, "fs@example.org", UserRole.FORMER_STUDENT);
    var admin = newRole(u, "adm@example.org", UserRole.ADMIN);
    em.flush();
    em.clear();

    assertTrue(repo.existsFormerStudentForAnother(u.getId(), admin.getId()));
    assertFalse(repo.existsFormerStudentForAnother(u.getId(), fs.getId()));
  }

  @Test
  @TestTransaction
  void negative_when_no_data() {
    assertFalse(repo.existsByEmail("none@example.org"));
    assertTrue(repo.findByEmail("none@example.org").isEmpty());
    assertFalse(repo.existsFormerStudentByUserId(UUID.randomUUID()));
    assertFalse(repo.existsByEmailForAnother("none@example.org", UUID.randomUUID()));
    assertFalse(repo.existsFormerStudentForAnother(UUID.randomUUID(), UUID.randomUUID()));
  }
}
