package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.builders.AccountBuilder;
import br.org.catolicasc.pug.builders.AdminBuilder;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.AdminRepository;
import br.org.catolicasc.pug.identity.service.AccountService;
import br.org.catolicasc.pug.identity.service.dtos.AdminCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AdminUpdateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
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
@DisplayName("AdminServiceImpl Coverage Tests")
class AdminServiceImplTest {

  @Mock AdminRepository repo;
  @Mock AccountService accountService;
  @Mock AuditPublisher audit;
  @InjectMocks AdminServiceImpl service;

  @Nested
  @DisplayName("Method: save")
  class SaveTests {
    @Test
    @DisplayName("Should save admin successfully and fire audit")
    void success() {
      var account = AccountBuilder.anAccount().build();

      var userCmd =
          new br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand(
              "11144477735", "Admin User");
      var accCmd =
          new br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand(
              "admin@pug.com", AccountType.ADMIN, "pass", userCmd);
      var cmd = new AdminCreateCommand(accCmd, Campi.JARAGUA_DO_SUL);

      when(accountService.save(any())).thenReturn(account);
      when(repo.persist(any())).thenAnswer(i -> i.getArgument(0));

      Admin saved = service.save(cmd);

      assertThat(saved.getAccountId()).isEqualTo(account.getId());
      verify(repo).persist(any());
      verify(audit).fireCreate(eq(Admin.class.getName()), any());
    }
  }

  @Nested
  @DisplayName("Method: delete")
  class DeleteTests {
    @Test
    @DisplayName("Should delete admin and revoke account")
    void deleteSuccess() {
      UUID id = UUID.randomUUID();
      when(repo.deleteByAccountId(id)).thenReturn(true);

      boolean deleted = service.delete(id);

      assertThat(deleted).isTrue();
      verify(accountService).delete(id);
      verify(audit).fireDelete(Admin.class.getName(), id);
    }

    @Test
    @DisplayName("Should do nothing if admin not found")
    void deleteFail() {
      UUID id = UUID.randomUUID();
      when(repo.deleteByAccountId(id)).thenReturn(false);

      boolean deleted = service.delete(id);

      assertThat(deleted).isFalse();
      verify(accountService, never()).delete(any());
    }
  }

  @Nested
  @DisplayName("Method: update")
  class UpdateTests {
    @Test
    @DisplayName("Should update campus successfully")
    void updateSuccess() {
      UUID id = UUID.randomUUID();
      Admin current = AdminBuilder.anAdmin().forAccount(id).atCampus(Campi.JARAGUA_DO_SUL).build();
      Admin updatedExpected = current.changeCampus(Campi.JOINVILLE);

      when(repo.findOptionalByAccountId(id))
          .thenReturn(Optional.of(current))
          .thenReturn(Optional.of(updatedExpected));

      Admin updated = service.update(id, new AdminUpdateCommand(null, Campi.JOINVILLE));

      assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
      verify(repo).update(any());
      verify(audit).fireUpdate(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFound when admin missing")
    void notFound() {
      when(repo.findOptionalByAccountId(any())).thenReturn(Optional.empty());
      org.junit.jupiter.api.Assertions.assertThrows(
          ResourceNotFoundException.class,
          () -> service.update(UUID.randomUUID(), new AdminUpdateCommand(null, Campi.JOINVILLE)));
    }
  }

  @Nested
  @DisplayName("Method: deactivate")
  class DeactivateTests {
    @Test
    @DisplayName("Should deactivate linked account")
    void deactivateSuccess() {
      UUID id = UUID.randomUUID();
      Admin admin = AdminBuilder.anAdmin().forAccount(id).build();
      when(repo.findOptionalByAccountId(id)).thenReturn(Optional.of(admin));

      boolean result = service.deactivate(id);

      assertThat(result).isTrue();
      verify(accountService).deactivate(id);
    }
  }
}
