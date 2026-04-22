package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.UserRepository;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UserServiceImpl Coverage")
class UserServiceImplTest {

  @Inject UserServiceImpl service;
  @InjectMock UserRepository repository;
  @InjectMock AuditPublisher audit;

  @Test
  @DisplayName("Should create user successfully and fire audit")
  void saveSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserCreateCommand cmd = new UserCreateCommand(cpf, "New Name");
    when(repository.existsByCpf(any())).thenReturn(false);
    when(repository.persist(any())).thenAnswer(i -> i.getArgument(0));

    User saved = service.save(cmd);

    assertThat(saved.getName()).isEqualTo("New Name");
    verify(audit).fireCreate(User.class.getName(), saved.getId());
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException if CPF exists")
  void saveDuplicate() {
    UserCreateCommand userCmd =
        new UserCreateCommand(TestBrazilianIdentifierGenerator.generateValidCpf(), "Name");
    when(repository.existsByCpf(anyString())).thenReturn(true);

    Assertions.assertThrows(DuplicateResourceException.class, () -> service.save(userCmd));
  }

  @Test
  @DisplayName("Should throw AppValidationException for invalid input")
  void saveInvalid() {
    UserCreateCommand cmd = new UserCreateCommand("123", "Too Short");
    Assertions.assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should delete user and fire audit")
  void deleteSuccess() {
    UUID id = UUID.randomUUID();
    when(repository.deleteById(id)).thenReturn(true);

    boolean deleted = service.delete(id);

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(User.class.getName(), id);
  }

  @Test
  @DisplayName("Should update user successfully and fire audit")
  void updateSuccess() {
    UUID id = UUID.randomUUID();
    User user =
        User.factory(Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "Old Name");
    User updatedUser = user.rename("New Name");

    when(repository.findOptionalById(id))
        .thenReturn(Optional.of(user))
        .thenReturn(Optional.of(updatedUser));

    UserUpdateCommand cmd = new UserUpdateCommand("New Name");
    User result = service.update(id, cmd);

    assertThat(result.getName()).isEqualTo("New Name");
    verify(repository).update(any());
  }

  @Test
  @DisplayName("Should throw AppValidationException for invalid name")
  void updateInvalid() {
    UUID id = UUID.randomUUID();
    User user =
        User.factory(Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "Name");
    when(repository.findOptionalById(id)).thenReturn(Optional.of(user));

    String longName = "A".repeat(300);
    UserUpdateCommand cmd = new UserUpdateCommand(longName);

    Assertions.assertThrows(AppValidationException.class, () -> service.update(id, cmd));
  }

  @Test
  @DisplayName("Should successfully bulk create users")
  void saveInBulkSuccess() {
    List<UserCreateCommand> cmds =
        List.of(new UserCreateCommand(TestBrazilianIdentifierGenerator.generateValidCpf(), "Name"));
    when(repository.existsAnyByCpfs(any())).thenReturn(false);
    when(repository.persistAll(any())).thenAnswer(i -> i.getArgument(0));

    List<User> saved = service.saveInBulk(cmds);

    assertThat(saved).hasSize(1);
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException on bulk duplicate")
  void saveInBulkDuplicate() {
    List<UserCreateCommand> cmds =
        List.of(new UserCreateCommand(TestBrazilianIdentifierGenerator.generateValidCpf(), "Name"));
    when(repository.existsAnyByCpfs(any())).thenReturn(true);

    Assertions.assertThrows(DuplicateResourceException.class, () -> service.saveInBulk(cmds));
  }

  @Test
  @DisplayName("Should return list of users by CPFs")
  void listByCpfs() {
    when(repository.listByCpfs(any())).thenReturn(List.of());
    assertThat(service.listByCpfs(List.of(TestBrazilianIdentifierGenerator.generateValidCpf())))
        .isEmpty();
  }

  @Test
  @DisplayName("Should return user by ID")
  void getByIdSuccess() {
    UUID id = UUID.randomUUID();
    User user =
        User.factory(Cpf.factory(TestBrazilianIdentifierGenerator.generateValidCpf()), "Name");
    when(repository.findOptionalById(id)).thenReturn(Optional.of(user));

    assertThat(service.getById(id)).isEqualTo(user);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException for unknown ID")
  void getByIdNotFound() {
    when(repository.findOptionalById(any())).thenReturn(Optional.empty());
    Assertions.assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
  }

  @Test
  @DisplayName("Should perform batch delete")
  void deleteAll() {
    List<UUID> ids = List.of(UUID.randomUUID());
    when(repository.deleteAllByIds(ids)).thenReturn(1L);

    long count = service.deleteAll(ids);
    assertThat(count).isEqualTo(1L);
  }
}
