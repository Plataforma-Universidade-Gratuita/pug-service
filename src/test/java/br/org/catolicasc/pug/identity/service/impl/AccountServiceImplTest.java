package br.org.catolicasc.pug.identity.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.AccountCreateCommandBuilder.anAccountCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.AccountUpdateCommandBuilder.anAccountUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.AccountRepository;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.domain.vos.Cpf;
import br.org.catolicasc.pug.identity.domain.vos.Email;
import br.org.catolicasc.pug.identity.service.UserService;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AccountServiceImpl Coverage")
class AccountServiceImplTest {

  @Inject AccountServiceImpl service;
  @InjectMock AccountRepository repository;
  @InjectMock UserService userService;
  @InjectMock AuditPublisher audit;

  @Test
  @DisplayName("Should provision new user and account when user does not exist")
  void saveNewUser() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    AccountCreateCommand cmd =
        anAccountCreateCommand()
            .withEmail("new@pug.com")
            .withType(AccountType.STUDENT)
            .withPasswordHash("pass")
            .withUserCpf(cpf)
            .withUserName("New User")
            .build();

    when(userService.existsByCpf(any())).thenReturn(false);
    User createdUser = User.factory(Cpf.factory(cpf), "New User");
    when(userService.save(any())).thenReturn(createdUser);
    when(repository.persist(any(Account.class))).thenAnswer(i -> i.getArgument(0));

    Account saved = service.save(cmd);

    assertThat(saved.getUserId()).isEqualTo(createdUser.getId());
    verify(repository).persist(any());
    verify(audit).fireCreate(any(), any());
  }

  @Test
  @DisplayName("Should delete account and prune orphan user")
  void deleteAndPrune() {
    Account acc =
        Account.factory(
            UUID.randomUUID(),
            br.org.catolicasc.pug.identity.domain.vos.Email.factory("a@a.com"),
            AccountType.STUDENT,
            "hash");

    when(repository.findOptionalById(acc.getId())).thenReturn(Optional.of(acc));
    when(repository.countAllAccountsByUserId(acc.getUserId())).thenReturn(1L);
    when(repository.deleteById(acc.getId())).thenReturn(true);

    boolean deleted = service.delete(acc.getId());

    assertThat(deleted).isTrue();
    verify(userService).delete(acc.getUserId());
    verify(audit).fireDelete(any(), any());
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException on email conflict")
  void duplicateEmail() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    when(userService.existsByCpf(any())).thenReturn(false);
    when(userService.save(any())).thenReturn(User.factory(Cpf.factory(cpf), "Name"));
    when(repository.existsByEmail(any())).thenReturn(true);

    AccountCreateCommand cmd =
        anAccountCreateCommand()
            .withEmail("exists@pug.com")
            .withType(AccountType.STUDENT)
            .withPasswordHash("p")
            .withUserCpf(cpf)
            .withUserName("N")
            .build();

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should update account and user successfully")
  void updateSuccess() {
    UUID id = UUID.randomUUID();
    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory("a@a.com"), AccountType.STUDENT, "hash");
    Account updatedAcc = acc.changeEmail(Email.factory("new@email.com"));

    when(repository.findOptionalById(id))
        .thenReturn(Optional.of(acc))
        .thenReturn(Optional.of(updatedAcc));

    var cmd = anAccountUpdateCommand().withEmail("new@email.com").withUserName(null).build();

    Account updated = service.update(id, cmd);

    assertThat(updated.getEmail().getValue()).isEqualTo("new@email.com");
    verify(repository).update(any());
    verify(audit).fireUpdate(eq(Account.class.getName()), eq(id), any(), any());
  }

  @Test
  @DisplayName("Should save accounts in bulk successfully")
  void saveInBulkSuccess() {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    AccountCreateCommand cmd =
        anAccountCreateCommand()
            .withEmail("a@a.com")
            .withType(AccountType.STUDENT)
            .withPasswordHash("hash")
            .withUserCpf(cpf)
            .withUserName("Name")
            .build();

    when(repository.existsAnyByEmails(any())).thenReturn(false);
    when(userService.listByCpfs(any())).thenReturn(List.of());
    when(userService.saveInBulk(any())).thenReturn(List.of(User.factory(Cpf.factory(cpf), "Name")));
    when(repository.persistAll(any())).thenAnswer(i -> i.getArgument(0));

    List<Account> saved = service.saveInBulk(List.of(cmd));

    assertThat(saved).hasSize(1);
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException on bulk duplicate email")
  void saveInBulkDuplicate() {
    AccountCreateCommand cmd =
        anAccountCreateCommand()
            .withEmail("dup@a.com")
            .withType(AccountType.STUDENT)
            .withPasswordHash("hash")
            .withoutUser()
            .build();
    when(repository.existsAnyByEmails(any())).thenReturn(true);

    assertThrows(DuplicateResourceException.class, () -> service.saveInBulk(List.of(cmd)));
  }

  @Test
  @DisplayName("Should get account by email successfully")
  void getByEmailSuccess() {
    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory("a@a.com"), AccountType.STUDENT, "hash");
    when(repository.findOptionalByEmail("a@a.com")).thenReturn(Optional.of(acc));

    assertThat(service.getByEmail("a@a.com").getEmail().getValue()).isEqualTo("a@a.com");
  }

  @Test
  @DisplayName("Should batch delete multiple accounts")
  void deleteAllSuccess() {
    List<UUID> ids = List.of(UUID.randomUUID());
    when(repository.findUserIdsByIds(ids)).thenReturn(List.of(UUID.randomUUID()));
    when(repository.deleteAllByIds(ids)).thenReturn(1L);
    when(repository.findAllOrphanUserIdsByUserIds(any())).thenReturn(List.of(UUID.randomUUID()));

    long deleted = service.deleteAll(ids);

    assertThat(deleted).isEqualTo(1L);
    verify(userService).deleteAll(any());
  }

  @Test
  @DisplayName("Should deactivate account")
  void deactivateSuccess() {
    Account acc =
        Account.factory(UUID.randomUUID(), Email.factory("a@a.com"), AccountType.STUDENT, "hash");
    when(repository.findOptionalById(acc.getId())).thenReturn(Optional.of(acc));

    Account deactivated = service.deactivate(acc.getId());

    assertThat(deactivated.getActive()).isFalse();
    verify(repository).update(any());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }
}
