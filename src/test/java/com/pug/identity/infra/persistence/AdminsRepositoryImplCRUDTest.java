package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.helpers.entityGenerators.AdminsEntityGenerator;
import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.domain.AdminsRepository;
import com.pug.identity.domain.UsersRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminsRepositoryImplCRUDTest {

  @Inject AdminsRepository adminsRepo;
  @Inject UsersRepository usersRepo;

  private final UsersEntityGenerator userGen = new UsersEntityGenerator();
  private final AdminsEntityGenerator adminGen = new AdminsEntityGenerator();

  private UsersEntity persistRandomUser(String hint) {
    UsersEntity u = userGen.createRandomUsersEntity();
    u.setEmail("admin_" + hint + "_" + System.nanoTime() + "@example.com");
    usersRepo.persist(u);
    return u;
  }

  @Test
  void persist_and_find_by_id() {
    UsersEntity u = persistRandomUser("one");
    AdminsEntity a = adminGen.createRandomAdminsEntity(u);

    adminsRepo.persist(a);

    var found = adminsRepo.findOptionalById(u.getId());
    assertTrue(found.isPresent());
    assertEquals(u.getId(), found.get().getUserId());
    assertNotNull(found.get().getUser());
    assertEquals(u.getEmail(), found.get().getUser().getEmail());
    assertNotNull(found.get().getGrantedAt());
  }

  @Test
  void existsByUserId_true_false() {
    UsersEntity u = persistRandomUser("exists");
    AdminsEntity a = adminGen.createRandomAdminsEntity(u);
    adminsRepo.persist(a);

    assertTrue(adminsRepo.existsByUserId(u.getId()));
    assertFalse(adminsRepo.existsByUserId(UUID.randomUUID()));
  }

  @Test
  void listAllAdmins_returns_rows_with_joined_user() {
    UsersEntity u1 = persistRandomUser("list1");
    UsersEntity u2 = persistRandomUser("list2");

    adminsRepo.persistAll(
        List.of(adminGen.createRandomAdminsEntity(u1), adminGen.createRandomAdminsEntity(u2)));

    var all = adminsRepo.listAllAdmins();
    assertTrue(all.size() >= 2);
    assertTrue(
        all.stream()
            .anyMatch(e -> e.getUser() != null && u1.getEmail().equals(e.getUser().getEmail())));
    assertTrue(
        all.stream()
            .anyMatch(e -> e.getUser() != null && u2.getEmail().equals(e.getUser().getEmail())));
  }

  @Test
  void deleteByIds_deletes_and_returns_count() {
    UsersEntity u1 = persistRandomUser("del1");
    UsersEntity u2 = persistRandomUser("del2");
    UsersEntity u3 = persistRandomUser("del3");

    adminsRepo.persistAll(
        List.of(adminGen.createRandomAdminsEntity(u1), adminGen.createRandomAdminsEntity(u2)));

    long n = adminsRepo.deleteByIds(List.of(u1.getId(), u2.getId(), u3.getId()));
    assertEquals(2L, n);

    assertTrue(adminsRepo.findOptionalById(u1.getId()).isEmpty());
    assertTrue(adminsRepo.findOptionalById(u2.getId()).isEmpty());
  }

  @Test
  void persistAll_inserts_multiple() {
    UsersEntity u1 = persistRandomUser("bulk1");
    UsersEntity u2 = persistRandomUser("bulk2");
    UsersEntity u3 = persistRandomUser("bulk3");

    adminsRepo.persistAll(
        List.of(
            adminGen.createRandomAdminsEntity(u1),
            adminGen.createRandomAdminsEntity(u2),
            adminGen.createRandomAdminsEntity(u3)));

    assertTrue(adminsRepo.existsByUserId(u1.getId()));
    assertTrue(adminsRepo.existsByUserId(u2.getId()));
    assertTrue(adminsRepo.existsByUserId(u3.getId()));
  }

  @Test
  void persist_by_id_only_fk_path() {
    UsersEntity u = persistRandomUser("byid");
    AdminsEntity a = adminGen.createRandomAdminsEntity(u.getId());
    a.setGrantedAt(OffsetDateTime.now());

    adminsRepo.persist(a);

    var found = adminsRepo.findOptionalById(u.getId());
    assertTrue(found.isPresent());
    assertEquals(u.getEmail(), found.get().getUser().getEmail());
  }
}
