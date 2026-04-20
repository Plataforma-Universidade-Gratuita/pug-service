package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.AccountRepository;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.service.UserService;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
@DisplayName("AccountServiceImpl Coverage Tests")
class AccountServiceImplTest {

  @Mock AccountRepository repo;
  @Mock UserService userService;
  @Mock AuditPublisher audit;
  @InjectMocks AccountServiceImpl service;

  @Nested
  @DisplayName("Method: save")
  class SaveTests {
    @Test
    @DisplayName("Should provision new user and account when user does not exist")
    void shouldProvisionNewUserAndAccount() {
      UserCreateCommand userCmd = new UserCreateCommand("11144477735", "New User");
      AccountCreateCommand cmd =
          new AccountCreateCommand("new@pug.com", AccountType.STUDENT, "pass", userCmd);

      when(userService.existsByCpf(any())).thenReturn(false);
      User createdUser = User.factory(Cpf.factory("11144477735"), "New User");
      when(userService.save(any())).thenReturn(createdUser);
      when(repo.persist(any(Account.class))).thenAnswer(i -> i.getArgument(0));

      Account saved = service.save(cmd);

      assertThat(saved.getUserId()).isEqualTo(createdUser.getId());
      verify(repo).persist(any());
      verify(audit).fireCreate(any(), any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException if email exists")
    void shouldThrowOnDuplicateEmail() {
      User user = User.factory(Cpf.factory("11144477735"), "Name");
      when(userService.existsByCpf(any())).thenReturn(true);
      when(userService.getByCpf(any())).thenReturn(user);
      when(repo.existsByEmail(any())).thenReturn(true);

      AccountCreateCommand cmd =
          new AccountCreateCommand(
              "exists@pug.com",
              AccountType.STUDENT,
              "p",
              new UserCreateCommand("11144477735", "N"));

      org.junit.jupiter.api.Assertions.assertThrows(
          DuplicateResourceException.class, () -> service.save(cmd));
    }
  }

  @Nested
  @DisplayName("Method: delete")
  class DeleteTests {
    @Test
    @DisplayName("Should delete account and prune orphan user")
    void shouldDeleteAndPrune() {
      Account acc =
          Account.factory(
              UUID.randomUUID(),
              br.org.catolicasc.pug.identity.domain.vos.Email.factory("a@a.com"),
              AccountType.STUDENT,
              "p");
      when(repo.findOptionalById(acc.getId())).thenReturn(Optional.of(acc));
      when(repo.countAllAccountsByUserId(acc.getUserId())).thenReturn(1L);
      when(repo.deleteById(acc.getId())).thenReturn(true);

      boolean deleted = service.delete(acc.getId());

      assertThat(deleted).isTrue();
      verify(userService).delete(acc.getUserId());
      verify(audit).fireDelete(any(), any());
    }
  }

  @Nested
  @DisplayName("Method: getById")
  class GetTests {
    @Test
    @DisplayName("Should throw ResourceNotFound when missing")
    void shouldThrowMissing() {
      when(repo.findOptionalById(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
    }
  }
}
