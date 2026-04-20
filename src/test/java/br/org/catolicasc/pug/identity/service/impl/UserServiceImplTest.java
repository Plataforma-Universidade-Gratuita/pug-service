package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.UserRepository;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Coverage Tests")
class UserServiceImplTest {

  @Mock UserRepository repo;
  @Mock AuditPublisher audit;
  @InjectMocks UserServiceImpl service;

  @Nested
  @DisplayName("Method: save")
  class SaveTests {
    @Test
    @DisplayName("Should successfully persist valid user")
    void success() {
      when(repo.existsByCpf(any())).thenReturn(false);
      when(repo.persist(any())).thenAnswer(i -> i.getArgument(0));

      User user = service.save(new UserCreateCommand("11144477735", "Valid Name"));

      assertThat(user.getName()).isEqualTo("Valid Name");
      verify(repo).persist(any());
      verify(audit).fireCreate(any(), any());
    }

    @Test
    @DisplayName("Should throw when CPF is already registered")
    void duplicate() {
      when(repo.existsByCpf(any())).thenReturn(true);
      org.junit.jupiter.api.Assertions.assertThrows(
          DuplicateResourceException.class,
          () -> service.save(new UserCreateCommand("11144477735", "Name")));
    }

    @Test
    @DisplayName("Should throw when validation fails in processor")
    void validationFail() {
      org.junit.jupiter.api.Assertions.assertThrows(
          AppValidationException.class, () -> service.save(new UserCreateCommand("INVALID", "")));
    }
  }

  @Nested
  @DisplayName("Method: delete")
  class DeleteTests {
    @Test
    @DisplayName("Should delete and audit when user exists")
    void success() {
      when(repo.deleteById(any())).thenReturn(true);
      boolean result = service.delete(UUID.randomUUID());
      assertThat(result).isTrue();
      verify(audit).fireDelete(any(), any());
    }

    @Test
    @DisplayName("Should return false when user does not exist")
    void notFound() {
      when(repo.deleteById(any())).thenReturn(false);
      boolean result = service.delete(UUID.randomUUID());
      assertThat(result).isFalse();
      verify(audit, never()).fireDelete(any(), any());
    }
  }

  @Nested
  @DisplayName("Method: update")
  class UpdateTests {
    @Test
    @DisplayName("Should update name successfully")
    void updateName() {
      UUID id = UUID.randomUUID();
      User existing = User.factory(Cpf.factory("11144477735"), "Old Name");

      when(repo.findOptionalById(id)).thenReturn(Optional.of(existing));

      User updatedExpected = existing.rename("New Name");
      when(repo.findOptionalById(id)).thenReturn(Optional.of(updatedExpected));

      User updated = service.update(id, new UserUpdateCommand("New Name"));

      assertThat(updated.getName()).isEqualTo("New Name");
      verify(repo).update(any());
      verify(audit).fireUpdate(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw when user not found during update")
    void notFound() {
      when(repo.findOptionalById(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class,
          () -> service.update(UUID.randomUUID(), new UserUpdateCommand("New")));
    }
  }
}
