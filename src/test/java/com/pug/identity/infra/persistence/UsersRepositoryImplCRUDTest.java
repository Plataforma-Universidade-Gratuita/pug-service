package com.pug.identity.infra.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.domain.UsersRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UsersRepositoryImplCRUDTest {

  @Inject UsersRepository usersRepository;

  private final UsersEntityGenerator gen = new UsersEntityGenerator();

  @Test
  @Transactional
  public void testPersistUser() {
    UsersEntity u = gen.createRandomUsersEntity();
    usersRepository.persist(u);

    Optional<UsersEntity> result = usersRepository.findOptionalById(u.getId());
    assertTrue(result.isPresent());
    assertEquals(u.getName(), result.get().getName());
    assertEquals(u.getEmail(), result.get().getEmail());
  }

  @Test
  @Transactional
  public void testFindUserById() {
    UsersEntity u = gen.createRandomUsersEntity();
    usersRepository.persist(u);

    Optional<UsersEntity> found = usersRepository.findOptionalById(u.getId());
    assertTrue(found.isPresent());
    assertEquals(u.getId(), found.get().getId());
  }

  @Test
  @Transactional
  public void testFindUserByEmail() {
    UsersEntity u = gen.createRandomUsersEntity();
    usersRepository.persist(u);

    Optional<UsersEntity> found = usersRepository.findOptionalByEmail(u.getEmail());
    assertTrue(found.isPresent());
    assertEquals(u.getEmail(), found.get().getEmail());
  }

  @Test
  @Transactional
  public void testUserNotFoundByEmail() {
    Optional<UsersEntity> ghost = usersRepository.findOptionalByEmail("ghost@example.com");
    assertFalse(ghost.isPresent());
  }

  @Test
  @Transactional
  public void testPersistAll() {
    List<UsersEntity> users =
        List.of(
            gen.createRandomUsersEntity(),
            gen.createRandomUsersEntity(),
            gen.createRandomUsersEntity());

    usersRepository.persistAll(users);

    for (UsersEntity u : users) {
      assertTrue(usersRepository.findOptionalById(u.getId()).isPresent());
    }
  }

  @Test
  public void testDeleteByIds_single() {
    UsersEntity u1 = gen.createRandomUsersEntity();
    UsersEntity u2 = gen.createRandomUsersEntity();
    usersRepository.persistAll(List.of(u1, u2));

    long deleted = usersRepository.deleteByIds(List.of(u1.getId()));
    assertEquals(1L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
    assertTrue(usersRepository.findOptionalById(u2.getId()).isPresent());
  }

  @Test
  public void testDeleteByIds_multiple() {
    UsersEntity u1 = gen.createRandomUsersEntity();
    UsersEntity u2 = gen.createRandomUsersEntity();
    UsersEntity u3 = gen.createRandomUsersEntity();
    usersRepository.persistAll(List.of(u1, u2, u3));

    long deleted = usersRepository.deleteByIds(List.of(u1.getId(), u3.getId()));
    assertEquals(2L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
    assertFalse(usersRepository.findOptionalById(u3.getId()).isPresent());
    assertTrue(usersRepository.findOptionalById(u2.getId()).isPresent());
  }

  @Test
  public void testDeleteByIds_mixedWithNonExisting() {
    UsersEntity u1 = gen.createRandomUsersEntity();
    usersRepository.persist(u1);

    java.util.UUID ghost = java.util.UUID.randomUUID();
    long deleted = usersRepository.deleteByIds(List.of(u1.getId(), ghost));
    assertEquals(1L, deleted);

    assertFalse(usersRepository.findOptionalById(u1.getId()).isPresent());
  }

  @Test
  @Transactional
  public void testExistsByEmail() {
    UsersEntity u = gen.createRandomUsersEntity();
    usersRepository.persist(u);
    assertTrue(usersRepository.existsByEmail(u.getEmail()));
    assertFalse(usersRepository.existsByEmail("nope@example.com"));
  }

  @Test
  @Transactional
  public void testExistsAnyByEmailIn() {
    UsersEntity u1 = gen.createRandomUsersEntity();
    UsersEntity u2 = gen.createRandomUsersEntity();
    usersRepository.persistAll(List.of(u1, u2));

    assertTrue(usersRepository.existsAnyByEmailIn(List.of(u1.getEmail(), "x@y.z")));
    assertFalse(usersRepository.existsAnyByEmailIn(List.of("x@y.z", "y@x.z")));
    assertFalse(usersRepository.existsAnyByEmailIn(List.of()));
  }

  @Test
  @Transactional
  public void testListByCpf_multiple() {
    UsersEntity a = gen.createRandomUsersEntity();
    UsersEntity b = gen.createRandomUsersEntity();
    b.setCpf(a.getCpf());
    usersRepository.persistAll(List.of(a, b));

    var list = usersRepository.listByCpf(a.getCpf());
    assertTrue(list.size() >= 2);
    assertTrue(list.stream().anyMatch(u -> u.getEmail().equals(a.getEmail())));
    assertTrue(list.stream().anyMatch(u -> u.getEmail().equals(b.getEmail())));
  }
}
