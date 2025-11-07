package com.pug.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.domain.AdminsRepository;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.infra.persistence.AdminsEntity;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AdminsServiceTest {

  @Inject AdminsService adminsService;
  @Inject AdminsRepository adminsRepo;
  @Inject UsersRepository usersRepo;

  private final UsersEntityGenerator userGen = new UsersEntityGenerator();

  private UsersEntity persistRandomUser(String hint) {
    UsersEntity u = userGen.createRandomUsersEntity();
    u.setEmail("admin_" + hint + "_" + System.nanoTime() + "@example.com");
    usersRepo.persist(u);
    return u;
  }

  @Test
  @Transactional
  void grant_ok_creates_admin_row() {
    UsersEntity u = persistRandomUser("grant_ok");

    var admin = adminsService.grant(u.getId());

    assertNotNull(admin);
    assertTrue(adminsRepo.existsByUserId(u.getId()));
    var userAfter = usersRepo.findOptionalById(u.getId()).orElseThrow();
    assertTrue(userAfter.getActive());
  }

  @Test
  void grant_nonexistent_user_throws() {
    UUID ghost = UUID.randomUUID();
    assertThrows(ResourceNotFoundException.class, () -> adminsService.grant(ghost));
  }

  @Test
  @Transactional
  void grant_duplicate_throws() {
    UsersEntity u = persistRandomUser("grant_dup");
    adminsService.grant(u.getId());
    assertThrows(DuplicateResourceException.class, () -> adminsService.grant(u.getId()));
  }

  @Test
  @Transactional
  void revoke_deletes_admin_and_deactivates_user() {
    UsersEntity u = persistRandomUser("revoke_ok");
    adminsService.grant(u.getId());

    adminsService.revoke(u.getId());

    assertTrue(adminsRepo.findOptionalById(u.getId()).isEmpty());
    var userAfter = usersRepo.findOptionalById(u.getId()).orElseThrow();
    assertFalse(userAfter.getActive());
  }

  @Test
  @Transactional
  void revoke_not_admin_throws() {
    UsersEntity u = persistRandomUser("revoke_na");
    assertThrows(ResourceNotFoundException.class, () -> adminsService.revoke(u.getId()));
  }

  @Test
  @Transactional
  void listAll() {
    UsersEntity u = persistRandomUser("list_all1");
    UsersEntity u2 = persistRandomUser("list_all2");
    adminsService.grant(u.getId());
    adminsService.grant(u2.getId());
    var admins = adminsService.listAll();
    assertNotNull(admins);
    assertTrue(admins.size() >= 2);
  }

  @Test
  @Transactional
  void reactivate_activates_user_and_creates_admin_if_missing() {
    UsersEntity u = persistRandomUser("reactivate_new");
    u.setActive(false);

    var out = adminsService.reactivate(u.getId());

    assertNotNull(out);
    assertTrue(adminsRepo.existsByUserId(u.getId()));
    var userAfter = usersRepo.findOptionalById(u.getId()).orElseThrow();
    assertTrue(userAfter.getActive());
  }

  @Test
  @Transactional
  void reactivate_idempotent_when_already_admin_and_active() {
    UsersEntity u = persistRandomUser("reactivate_idem");
    adminsService.grant(u.getId());

    OffsetDateTime beforeGranted =
        adminsRepo.findOptionalById(u.getId()).map(AdminsEntity::getGrantedAt).orElseThrow();

    var out = adminsService.reactivate(u.getId());

    assertNotNull(out);
    assertTrue(adminsRepo.existsByUserId(u.getId()));
    OffsetDateTime afterGranted =
        adminsRepo.findOptionalById(u.getId()).map(AdminsEntity::getGrantedAt).orElseThrow();
    assertEquals(beforeGranted, afterGranted);
    assertTrue(usersRepo.findOptionalById(u.getId()).orElseThrow().getActive());
  }

  @Test
  void reactivate_nonexistent_user_throws() {
    UUID ghost = UUID.randomUUID();
    assertThrows(ResourceNotFoundException.class, () -> adminsService.reactivate(ghost));
  }

  @Test
  @Transactional
  void grant_then_revoke_then_reactivate_roundtrip() {
    UsersEntity u = persistRandomUser("roundtrip");
    adminsService.grant(u.getId());
    adminsService.revoke(u.getId());

    assertTrue(adminsRepo.findOptionalById(u.getId()).isEmpty());
    assertFalse(usersRepo.findOptionalById(u.getId()).orElseThrow().getActive());

    var admin = adminsService.reactivate(u.getId());
    assertNotNull(admin);
    assertTrue(adminsRepo.existsByUserId(u.getId()));
    assertTrue(usersRepo.findOptionalById(u.getId()).orElseThrow().getActive());
  }
}
