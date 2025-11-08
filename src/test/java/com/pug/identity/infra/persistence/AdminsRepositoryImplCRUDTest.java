package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.Admin;
import com.pug.identity.domain.AdminsRepository;
import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminsRepositoryImplCRUDTest {

  @Inject AdminsRepository adminsRepo;
  @Inject UsersRepository usersRepo;

  private User persistRandomUser(String hint) {
    User u =
        User.builder()
            .cpf(new Cpf("52998224725"))
            .name("Admin " + hint)
            .email(new Email("admin_" + hint + "_" + System.nanoTime() + "@example.com"))
            .accountType(AccountType.ADMIN)
            .passwordHash("x")
            .active(true)
            .build();
    return usersRepo.persist(u);
  }

  @Test
  @Transactional
  void persist_and_find_by_id() {
    User u = persistRandomUser("one");
    Admin a = Admin.builder().user(u).grantedAt(OffsetDateTime.now()).build();

    Admin saved = adminsRepo.persist(a);

    var found = adminsRepo.findOptionalById(u.getId());
    assertTrue(found.isPresent());
    assertEquals(saved.getUser().getId(), found.get().getUser().getId());
    assertNotNull(found.get().getUser());
    assertEquals(u.getEmail().toString(), found.get().getUser().getEmail().toString());
    assertNotNull(found.get().getGrantedAt());
  }

  @Test
  @Transactional
  void existsByUserId_true_false() {
    User u = persistRandomUser("exists");
    adminsRepo.persist(Admin.builder().user(u).grantedAt(OffsetDateTime.now()).build());

    assertTrue(adminsRepo.existsByUserId(u.getId()));
    assertFalse(adminsRepo.existsByUserId(UUID.randomUUID()));
  }

  @Test
  @Transactional
  void listAllAdmins_returns_rows_with_joined_user() {
    User u1 = persistRandomUser("list1");
    User u2 = persistRandomUser("list2");

    adminsRepo.persistAll(
        List.of(
            Admin.builder().user(u1).grantedAt(OffsetDateTime.now()).build(),
            Admin.builder().user(u2).grantedAt(OffsetDateTime.now()).build()));

    var all = adminsRepo.listAllAdmins();
    assertTrue(all.size() >= 2);
    assertTrue(
        all.stream()
            .anyMatch(a -> u1.getEmail().toString().equals(a.getUser().getEmail().toString())));
    assertTrue(
        all.stream()
            .anyMatch(a -> u2.getEmail().toString().equals(a.getUser().getEmail().toString())));
  }

  @Test
  @Transactional
  void deleteByIds_deletes_and_returns_count() {
    User u1 = persistRandomUser("del1");
    User u2 = persistRandomUser("del2");
    User u3 = persistRandomUser("del3");

    adminsRepo.persistAll(
        List.of(
            Admin.builder().user(u1).grantedAt(OffsetDateTime.now()).build(),
            Admin.builder().user(u2).grantedAt(OffsetDateTime.now()).build()));

    long n = adminsRepo.deleteByIds(List.of(u1.getId(), u2.getId(), u3.getId()));
    assertEquals(2L, n);

    assertTrue(adminsRepo.findOptionalById(u1.getId()).isEmpty());
    assertTrue(adminsRepo.findOptionalById(u2.getId()).isEmpty());
  }

  @Test
  @Transactional
  void persistAll_inserts_multiple() {
    User u1 = persistRandomUser("bulk1");
    User u2 = persistRandomUser("bulk2");
    User u3 = persistRandomUser("bulk3");

    var saved =
        adminsRepo.persistAll(
            List.of(
                Admin.builder().user(u1).grantedAt(OffsetDateTime.now()).build(),
                Admin.builder().user(u2).grantedAt(OffsetDateTime.now()).build(),
                Admin.builder().user(u3).grantedAt(OffsetDateTime.now()).build()));

    assertEquals(3, saved.size());
    assertTrue(adminsRepo.existsByUserId(u1.getId()));
    assertTrue(adminsRepo.existsByUserId(u2.getId()));
    assertTrue(adminsRepo.existsByUserId(u3.getId()));
  }

  @Test
  @Transactional
  void persist_by_id_only_fk_path() {
    User u = persistRandomUser("byid");
    Admin a = Admin.builder().user(u).grantedAt(OffsetDateTime.now()).build();

    adminsRepo.persist(a);

    var found = adminsRepo.findOptionalById(u.getId());
    assertTrue(found.isPresent());
    assertEquals(u.getEmail().toString(), found.get().getUser().getEmail().toString());
  }
}
