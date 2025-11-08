package com.pug.identity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pug.helpers.domainGenerators.UserGenerator;
import com.pug.identity.domain.User;
import com.pug.identity.domain.UsersRepository;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.shared.domain.enums.AccountType;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UsersServiceTest {

  @Inject UsersService service;
  @InjectMock UsersRepository repo;

  private final UserGenerator userGen = new UserGenerator();

  @Test
  void save_newUser_persists_and_returns_domain() {
    User u = userGen.createRandomUser();
    when(repo.existsByEmail(u.getEmail().toString())).thenReturn(false);
    User saved = u.toBuilder().id(UUID.randomUUID()).createdAt(OffsetDateTime.now()).build();
    when(repo.persist(u)).thenReturn(saved);

    User out = service.save(u);

    assertNotNull(out.getId());
    assertEquals(saved.getEmail().toString(), out.getEmail().toString());
    verify(repo).existsByEmail(u.getEmail().toString());
    verify(repo).persist(u);
    verify(repo, never()).findOptionalByEmail(any());
  }

  @Test
  void save_duplicate_email_throws_DuplicateResourceException() {
    User u = userGen.createRandomUser();
    when(repo.existsByEmail(u.getEmail().toString())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.save(u));
    verify(repo, never()).persist(any());
  }

  @Test
  void saveAll_any_duplicate_email_throws() {
    List<User> users = List.of(userGen.createRandomUser(), userGen.createRandomUser());
    when(repo.existsAnyByEmailIn(anyCollection())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.saveAll(users));
    verify(repo, never()).persistAll(any());
  }

  @Test
  void saveAll_ok_persists_list_and_returns_saved() {
    List<User> batch = List.of(userGen.createRandomUser(), userGen.createRandomUser());
    when(repo.existsAnyByEmailIn(anyCollection())).thenReturn(false);
    List<User> saved =
        batch.stream().map(u -> u.toBuilder().id(UUID.randomUUID()).build()).toList();
    when(repo.persistAll(batch)).thenReturn(saved);

    List<User> out = service.saveAll(batch);

    assertEquals(2, out.size());
    assertTrue(out.stream().allMatch(u -> u.getId() != null));
    verify(repo).persistAll(batch);
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
    User current =
        userGen.createRandomUser().toBuilder().id(id).createdAt(OffsetDateTime.now()).build();
    User data = userGen.createRandomUser();
    User other =
        userGen.createRandomUser().toBuilder()
            .id(UUID.randomUUID())
            .createdAt(OffsetDateTime.now())
            .build();

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current));
    when(repo.findOptionalByEmail(data.getEmail().toString())).thenReturn(Optional.of(other));

    assertThrows(DuplicateResourceException.class, () -> service.update(id, data));
  }

  @Test
  void update_ok_copies_fields() {
    UUID id = UUID.randomUUID();
    User current =
        userGen.createRandomUser().toBuilder().id(id).createdAt(OffsetDateTime.now()).build();
    User data =
        User.builder()
            .id(id)
            .cpf(new Cpf(current.getCpf().toString()))
            .name(current.getName() + " Jr")
            .email(new Email(current.getEmail().toString()))
            .accountType(AccountType.STUDENT)
            .passwordHash("hash")
            .active(Boolean.TRUE)
            .createdAt(current.getCreatedAt())
            .build();

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current), Optional.of(data));
    when(repo.findOptionalByEmail(data.getEmail().toString())).thenReturn(Optional.empty());

    User out = service.update(id, data);

    assertEquals(data.getName(), out.getName());
    assertEquals(data.getEmail().toString(), out.getEmail().toString());
    assertEquals(data.getCpf().toString(), out.getCpf().toString());
    verify(repo).update(any(User.class));
  }

  @Test
  void deleteByIds_returns_count() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
    when(repo.deleteByIds(ids)).thenReturn(2L);

    long n = service.deleteByIds(ids);
    assertEquals(2L, n);
  }

  @Test
  void listAll_returns_domain() {
    User u1 = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    User u2 = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    when(repo.listAllUsers()).thenReturn(List.of(u1, u2));

    var out = service.listAll();
    assertEquals(2, out.size());
    assertTrue(
        out.stream().anyMatch(u -> u.getEmail().toString().equals(u1.getEmail().toString())));
  }

  @Test
  void listByCpf_returns_list() {
    User u = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    when(repo.listByCpf(u.getCpf().toString())).thenReturn(List.of(u));

    var out = service.listByCpf(u.getCpf().toString());
    assertEquals(1, out.size());
    assertEquals(u.getCpf().toString(), out.getFirst().getCpf().toString());
  }

  @Test
  void getById_success() {
    User u = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    when(repo.findOptionalById(u.getId())).thenReturn(Optional.of(u));

    var out = service.getById(u.getId());
    assertEquals(u.getEmail().toString(), out.getEmail().toString());
  }

  @Test
  void getById_not_found() {
    UUID id = UUID.randomUUID();
    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
  }

  @Test
  void getByEmail_success() {
    User u = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    when(repo.findOptionalByEmail(u.getEmail().toString())).thenReturn(Optional.of(u));

    var out = service.getByEmail(u.getEmail().toString());
    assertEquals(u.getCpf().toString(), out.getCpf().toString());
  }

  @Test
  void getByEmail_not_found() {
    when(repo.findOptionalByEmail("nobody@example.org")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getByEmail("nobody@example.org"));
  }

  @Test
  void search_maps_results() {
    User u1 = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();
    User u2 = userGen.createRandomUser().toBuilder().id(UUID.randomUUID()).build();

    when(repo.searchByName(anyString())).thenReturn(List.of(u1, u2));

    String query = " João ";
    String expectedKey = StringUtils.fold(query).toLowerCase(java.util.Locale.ROOT);

    var out = service.search(query);
    assertEquals(2, out.size());
    verify(repo).searchByName(expectedKey);
  }
}
