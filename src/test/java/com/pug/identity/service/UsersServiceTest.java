package com.pug.identity.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.helpers.entityGenerators.UsersEntityGenerator;
import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@QuarkusTest
class UsersServiceTest {

  @Inject UsersService service;
  @InjectMock UsersRepository repo;

  private final UserGenerator userGen = new UserGenerator();
  private final UsersEntityGenerator entGen = new UsersEntityGenerator();

  @Test
  void save_newUser_persists_and_returns_domain() {
    User u = userGen.createRandomUser();
    when(repo.existsByEmail(u.getEmail().toString())).thenReturn(false);

    doAnswer(
            inv -> {
              UsersEntity e = inv.getArgument(0);
              e.setId(UUID.randomUUID());
              e.setCreatedAt(OffsetDateTime.now());
              return null;
            })
        .when(repo)
        .persist(any(UsersEntity.class));

    User out = service.save(u);

    assertNotNull(out.getId());
    assertEquals(u.getCpf().toString(), out.getCpf().toString());
    assertEquals(u.getEmail().toString(), out.getEmail().toString());
    assertEquals(u.getName(), out.getName());

    ArgumentCaptor<UsersEntity> cap = ArgumentCaptor.forClass(UsersEntity.class);
    verify(repo).persist(cap.capture());
    assertEquals(u.getEmail().toString(), cap.getValue().getEmail());
  }

  @Test
  void save_duplicate_email_throws_DuplicateResourceException() {
    User u = userGen.createRandomUser();
    when(repo.existsByEmail(u.getEmail().toString())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.save(u));
  }

  @Test
  void saveAll_any_duplicate_email_throws() {
    List<User> users = List.of(userGen.createRandomUser(), userGen.createRandomUser());
    when(repo.existsAnyByEmailIn(anyCollection())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.saveAll(users));
  }

  @Test
  void saveAll_ok_persists_list() {
    List<User> users = List.of(userGen.createRandomUser(), userGen.createRandomUser());
    when(repo.existsAnyByEmailIn(anyCollection())).thenReturn(false);

    assertDoesNotThrow(() -> service.saveAll(users));
    verify(repo).persistAll(anyList());
  }

  @Test
  void update_not_found_throws_ResourceNotFound() {
    UUID id = UUID.randomUUID();
    User data = userGen.createRandomUser();
    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.update(id, data));
  }

  @Test
  void update_email_taken_by_other_throws_duplicate() {
    UUID id = UUID.randomUUID();

    UsersEntity current = entGen.createRandomUsersEntity();
    current.setId(id);
    current.setCreatedAt(OffsetDateTime.now());

    User data = userGen.createRandomUser();

    UsersEntity other = entGen.createRandomUsersEntity();
    other.setId(UUID.randomUUID());

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current));
    when(repo.findOptionalByEmail(data.getEmail().toString())).thenReturn(Optional.of(other));

    assertThrows(DuplicateResourceException.class, () -> service.update(id, data));
  }

  @Test
  void update_ok_copies_fields() {
    UUID id = UUID.randomUUID();

    UsersEntity current = entGen.createRandomUsersEntity();
    current.setId(id);
    current.setCreatedAt(OffsetDateTime.now());

    User data =
        User.builder()
            .id(id)
            .cpf(new Cpf(current.getCpf()))
            .name(current.getName() + " Jr")
            .email(new Email(current.getEmail()))
            .accountType(com.pug.shared.domain.enums.AccountType.STUDENT)
            .passwordHash("hash")
            .active(Boolean.TRUE)
            .createdAt(current.getCreatedAt())
            .build();

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current));
    when(repo.findOptionalByEmail(data.getEmail().toString())).thenReturn(Optional.empty());

    User out = service.update(id, data);

    assertEquals(data.getName(), out.getName());
    assertEquals(data.getEmail().toString(), out.getEmail().toString());
    assertEquals(data.getCpf().toString(), out.getCpf().toString());
  }

  @Test
  void deleteByIds_returns_count() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(repo.deleteByIds(ids)).thenReturn(2L);

    long n = service.deleteByIds(ids);
    assertEquals(2L, n);
  }

  @Test
  void listAll_maps_entities_to_domain() {
    UsersEntity e1 = entGen.createRandomUsersEntity();
    UsersEntity e2 = entGen.createRandomUsersEntity();
    e1.setCreatedAt(OffsetDateTime.now());
    e2.setCreatedAt(OffsetDateTime.now());

    when(repo.listAllUsers()).thenReturn(List.of(e1, e2));

    var out = service.listAll();
    assertEquals(2, out.size());
    assertTrue(out.stream().anyMatch(u -> u.getEmail().toString().equals(e1.getEmail())));
  }

  @Test
  void listByCpf_returns_list() {
    UsersEntity e = entGen.createRandomUsersEntity();
    e.setCreatedAt(OffsetDateTime.now());

    when(repo.listByCpf(e.getCpf())).thenReturn(List.of(e));

    var out = service.listByCpf(e.getCpf());
    assertEquals(1, out.size());
    assertEquals(e.getCpf(), out.getFirst().getCpf().toString());
  }

  @Test
  void getById_success() {
    UsersEntity e = entGen.createRandomUsersEntity();
    e.setId(UUID.randomUUID());
    e.setCreatedAt(OffsetDateTime.now());

    when(repo.findOptionalById(e.getId())).thenReturn(Optional.of(e));

    var out = service.getById(e.getId());
    assertEquals(e.getEmail(), out.getEmail().toString());
  }

  @Test
  void getById_not_found() {
    UUID id = UUID.randomUUID();
    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
  }

  @Test
  void getByEmail_success() {
    UsersEntity e = entGen.createRandomUsersEntity();
    e.setCreatedAt(OffsetDateTime.now());

    when(repo.findOptionalByEmail(e.getEmail())).thenReturn(Optional.of(e));

    var out = service.getByEmail(e.getEmail());
    assertEquals(e.getCpf(), out.getCpf().toString());
  }

  @Test
  void getByEmail_not_found() {
    when(repo.findOptionalByEmail("nobody@example.org")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByEmail("nobody@example.org"));
  }

  @Test
  void search_maps_results() {
    UsersEntity e1 = entGen.createRandomUsersEntity();
    UsersEntity e2 = entGen.createRandomUsersEntity();
    e1.setCreatedAt(OffsetDateTime.now());
    e2.setCreatedAt(OffsetDateTime.now());

    when(repo.searchByName(anyString())).thenReturn(List.of(e1, e2));

    var out = service.search(" João ");
    assertEquals(2, out.size());
  }
}
