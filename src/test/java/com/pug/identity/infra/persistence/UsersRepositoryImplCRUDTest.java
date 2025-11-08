package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UsersRepositoryImplCRUDTest {

  @Inject UsersRepository usersRepository;

  private User randomUser() {
    return User.builder()
        .cpf(new Cpf("52998224725"))
        .name("User " + System.nanoTime())
        .email(new Email("u" + UUID.randomUUID() + "@example.com"))
        .accountType(AccountType.ADMIN)
        .passwordHash("x")
        .active(true)
        .build();
  }

  @Test
  @Transactional
  public void testPersistUser() {
    User saved = usersRepository.persist(randomUser());

    Optional<User> result = usersRepository.findOptionalByEmail(saved.getEmail().toString());
    assertTrue(result.isPresent());
    assertEquals(saved.getName(), result.get().getName());
    assertEquals(saved.getEmail().toString(), result.get().getEmail().toString());
  }

  @Test
  @Transactional
  public void testFindUserById() {
    User saved = usersRepository.persist(randomUser());

    Optional<User> found = usersRepository.findOptionalById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(saved.getId(), found.get().getId());
  }

  @Test
  @Transactional
  public void testFindUserByEmail() {
    User saved = usersRepository.persist(randomUser());

    Optional<User> found = usersRepository.findOptionalByEmail(saved.getEmail().toString());
    assertTrue(found.isPresent());
    assertEquals(saved.getEmail().toString(), found.get().getEmail().toString());
  }

  @Test
  @Transactional
  public void testUserNotFoundByEmail() {
    Optional<User> ghost = usersRepository.findOptionalByEmail("ghost@example.com");
    assertFalse(ghost.isPresent());
  }

  @Test
  @Transactional
  public void testPersistAll() {
    List<User> toSave = List.of(randomUser(), randomUser(), randomUser());

    List<User> saved = usersRepository.persistAll(toSave);

    for (User u : saved) {
      assertTrue(usersRepository.findOptionalByEmail(u.getEmail().toString()).isPresent());
    }
  }

  @Test
  @Transactional
  public void testDeleteByIds_single() {
    User u1 = usersRepository.persist(randomUser());
    User u2 = usersRepository.persist(randomUser());

    long deleted = usersRepository.deleteByIds(List.of(u1.getId()));
    assertEquals(1L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
    assertTrue(usersRepository.findOptionalById(u2.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testDeleteByIds_multiple() {
    User u1 = usersRepository.persist(randomUser());
    User u2 = usersRepository.persist(randomUser());
    User u3 = usersRepository.persist(randomUser());

    long deleted = usersRepository.deleteByIds(List.of(u1.getId(), u3.getId()));
    assertEquals(2L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
    assertFalse(usersRepository.findOptionalById(u3.getId()).isPresent());
    assertTrue(usersRepository.findOptionalById(u2.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testDeleteByIds_mixedWithNonExisting() {
    User u1 = usersRepository.persist(randomUser());
    UUID ghost = UUID.randomUUID();

    long deleted = usersRepository.deleteByIds(List.of(u1.getId(), ghost));
    assertEquals(1L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testExistsByEmail() {
    User u = usersRepository.persist(randomUser());
    assertTrue(usersRepository.existsByEmail(u.getEmail().toString()));
    assertFalse(usersRepository.existsByEmail("nope@example.com"));
  }

  @Test
  @Transactional
  public void testExistsAnyByEmailIn() {
    User u1 = usersRepository.persist(randomUser());
    User u2 = usersRepository.persist(randomUser());

    assertTrue(usersRepository.existsAnyByEmailIn(List.of(u1.getEmail().toString(), "x@y.z")));
    assertFalse(usersRepository.existsAnyByEmailIn(List.of("x@y.z", "y@x.z")));
    assertFalse(usersRepository.existsAnyByEmailIn(List.of()));
  }

  @Test
  @Transactional
  public void testListByCpf_multiple() {
    User a =
        usersRepository.persist(
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("A")
                .email(new Email("a" + UUID.randomUUID() + "@example.com"))
                .accountType(AccountType.ADMIN)
                .passwordHash("x")
                .active(true)
                .build());
    User b =
        usersRepository.persist(
            User.builder()
                .cpf(new Cpf("52998224725"))
                .name("B")
                .email(new Email("b" + UUID.randomUUID() + "@example.com"))
                .accountType(AccountType.ADMIN)
                .passwordHash("x")
                .active(true)
                .build());

    var list = usersRepository.listByCpf("52998224725");
    assertTrue(list.size() >= 2);
    assertTrue(
        list.stream().anyMatch(u -> u.getEmail().toString().equals(a.getEmail().toString())));
    assertTrue(
        list.stream().anyMatch(u -> u.getEmail().toString().equals(b.getEmail().toString())));
  }

  @Test
  @Transactional
  public void testDeactivateById_setsActiveFalse() {
    User u = usersRepository.persist(randomUser());

    usersRepository.deactivateById(u.getId());

    var after = usersRepository.findOptionalById(u.getId()).orElseThrow();
    assertFalse(after.getActive());
  }

  @Test
  @Transactional
  public void testDeactivateById_idempotent() {
    User u = usersRepository.persist(randomUser());

    usersRepository.deactivateById(u.getId());
    usersRepository.deactivateById(u.getId());

    var after = usersRepository.findOptionalById(u.getId()).orElseThrow();
    assertFalse(after.getActive());
  }

  @Test
  public void testDeactivateById_nonExisting_noop() {
    User u = usersRepository.persist(randomUser());

    usersRepository.deactivateById(UUID.randomUUID());

    var after = usersRepository.findOptionalById(u.getId()).orElseThrow();
    assertTrue(after.getActive());
  }

  @Test
  public void testDeactivateById_alreadyInactive() {
    User u = usersRepository.persist(randomUser().toBuilder().active(false).build());

    usersRepository.deactivateById(u.getId());

    var after = usersRepository.findOptionalById(u.getId()).orElseThrow();
    assertFalse(after.getActive());
  }
}
