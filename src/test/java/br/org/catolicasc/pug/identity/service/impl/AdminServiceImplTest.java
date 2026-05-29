package br.org.catolicasc.pug.identity.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.AdminCreateCommandBuilder.anAdminCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.AdminUpdateCommandBuilder.anAdminUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.builders.domain.AccountBuilder;
import br.org.catolicasc.pug.helpers.builders.domain.AdminBuilder;
import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.domain.AdminRepository;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AdminServiceImpl Coverage")
class AdminServiceImplTest {

  @Inject AdminServiceImpl service;
  @InjectMock AdminRepository repo;
  @InjectMock AccountsService accountService;
  @InjectMock AuditPublisher audit;

  @Test
  @DisplayName("Should save admin successfully and fire audit")
  void saveSuccess() {
    var account = AccountBuilder.anAccount().build();
    var cmd = anAdminCreateCommand().withCampus(Campi.JARAGUA_DO_SUL).build();

    when(accountService.save(any())).thenReturn(account);
    when(repo.persist(any())).thenAnswer(i -> i.getArgument(0));

    Admin saved = service.save(cmd);

    assertThat(saved.getAccountId()).isEqualTo(account.getId());
    verify(repo).persist(any());
    verify(audit).fireCreate(Admin.class.getName(), account.getId());
  }

  @Test
  @DisplayName("Should delete admin and revoke account")
  void deleteSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(repo.deleteByAccountId(id)).thenReturn(true);

    boolean deleted = service.delete(id);

    assertThat(deleted).isTrue();
    verify(accountService).delete(id);
    verify(audit).fireDelete(Admin.class.getName(), id);
  }

  @Test
  @DisplayName("Should update campus successfully")
  void updateSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    Admin current = AdminBuilder.anAdmin().forAccount(id).atCampus(Campi.JARAGUA_DO_SUL).build();
    Admin updatedExpected = current.changeCampus(Campi.JOINVILLE);

    when(repo.findOptionalByAccountId(id))
        .thenReturn(Optional.of(current))
        .thenReturn(Optional.of(updatedExpected));

    Admin updated = service.update(id, anAdminUpdateCommand().withCampus(Campi.JOINVILLE).build());

    assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
  }

  @Nested
  @DisplayName("Method: deactivate")
  class DeactivateTests {
    @Test
    @DisplayName("Should deactivate linked account successfully")
    void deactivateSuccess() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      Admin admin = AdminBuilder.anAdmin().forAccount(id).build();
      when(repo.findOptionalByAccountId(id)).thenReturn(Optional.of(admin));

      boolean result = service.deactivate(id);

      assertThat(result).isTrue();
      verify(accountService).deactivate(id);
    }

    @Test
    @DisplayName("Should return false when admin not found during deactivation")
    void deactivateNotFound() {
      UUID id = UuidCreator.getTimeOrderedEpoch();
      when(repo.findOptionalByAccountId(id)).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class, () -> service.deactivate(id));
      verify(accountService, never()).deactivate(any());
    }

    @Test
    @DisplayName("Should return false when ID is null")
    void deactivateNullId() {
      boolean result = service.deactivate(null);
      assertThat(result).isFalse();
      verify(accountService, never()).deactivate(any());
    }
  }
}
