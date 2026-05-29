package br.org.catolicasc.pug.partner.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.StaffCreateCommandBuilder.aStaffCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.StaffUpdateCommandBuilder.aStaffUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.project.service.AttendanceService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffServiceImpl Coverage")
class StaffServiceImplTest {

  @Inject StaffServiceImpl service;
  @Inject TestDataFactory factory;
  @InjectMock AccountsService accountService;
  @InjectMock AuditPublisher audit;
  @InjectMock ProjectService projectService;
  @InjectMock AttendanceService attendanceService;

  @Test
  @Transactional
  @DisplayName("Should save staff successfully")
  void saveSuccess() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);

    var cmd =
        aStaffCreateCommand()
            .withEntityId(entity.getId())
            .withEmail(account.getEmail().getValue())
            .withoutUser()
            .build();

    when(accountService.save(any())).thenReturn(account);

    Staff saved = service.save(cmd);
    assertThat(saved.getEntityId()).isEqualTo(entity.getId());
    verify(audit).fireCreate(Staff.class.getName(), saved.getAccountId());
  }

  @Test
  @DisplayName("Should throw DuplicateResourceException when already assigned")
  void saveDuplicate() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(account, entity);

    var cmd =
        aStaffCreateCommand()
            .withEntityId(entity.getId())
            .withEmail(account.getEmail().getValue())
            .withoutUser()
            .build();
    when(accountService.save(any())).thenReturn(account);

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete staff successfully")
  void deleteSuccess() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(account, entity);

    when(projectService.existsByCreatedBy(account.getId())).thenReturn(false);

    boolean deleted = service.delete(account.getId());
    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Staff.class.getName(), account.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should update staff successfully")
  void updateSuccess() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(account, entity);

    var cmd =
        aStaffUpdateCommand().withName("New Name").withEmail(account.getEmail().getValue()).build();

    var updated = service.update(account.getId(), cmd);

    assertThat(updated).isNotNull();
    verify(accountService).update(eq(account.getId()), any());
    verify(audit).fireUpdate(eq(Staff.class.getName()), eq(account.getId()), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should deactivate staff successfully")
  void deactivateSuccess() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(account, entity);

    boolean result = service.deactivate(account.getId());

    assertThat(result).isTrue();
    verify(accountService).deactivate(account.getId());
  }

  @Test
  @DisplayName("Should get staff by account ID successfully")
  void getByAccountIdSuccess() {
    var entity = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(account, entity);

    Staff found = service.getByAccountId(account.getId());

    assertThat(found.getAccountId()).isEqualTo(account.getId());
    assertThat(found.getEntityId()).isEqualTo(entity.getId());
  }

  @Test
  @DisplayName("Should throw ResourceNotFound for unknown account ID")
  void getByAccountIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getByAccountId(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName(
      "Should throw Exception when staff account is already assigned to a DIFFERENT entity")
  void saveAssignedToOtherEntity() {
    var entityA = factory.createEntity(factory.getAnyCity());
    var entityB = factory.createEntity(factory.getAnyCity());
    var account = factory.createAccount(factory.createUser(), AccountType.PARTNER);

    factory.createStaff(account, entityA);

    var cmd =
        aStaffCreateCommand()
            .withEntityId(entityB.getId())
            .withEmail(account.getEmail().getValue())
            .withoutUser()
            .build();

    when(accountService.save(any())).thenReturn(account);

    assertThrows(BusinessRuleException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should return false when accountId is null on delete")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @DisplayName("Should throw exception when staff has created projects")
  void deleteHasProjects() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(projectService.existsByCreatedBy(id)).thenReturn(true);

    assertThrows(BusinessRuleException.class, () -> service.delete(id));
  }

  @Test
  @DisplayName("Should throw exception when staff has validated attendances")
  void deleteHasAttendances() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(projectService.existsByCreatedBy(id)).thenReturn(false);
    when(attendanceService.existsByValidatedBy(id)).thenReturn(true);

    assertThrows(BusinessRuleException.class, () -> service.delete(id));
  }

  @Test
  @Transactional
  @DisplayName("Should return 0 when entityId is null on batch delete")
  void deleteAllByEntityIdNull() {
    assertThat(service.deleteAllByEntityId(null)).isEqualTo(0);
  }

  @Test
  @Transactional
  @DisplayName("Should return 0 when no staff members found for entity")
  void deleteAllByEntityIdEmpty() {
    UUID entityId = UuidCreator.getTimeOrderedEpoch();
    assertThat(service.deleteAllByEntityId(entityId)).isEqualTo(0);
  }

  @Test
  @Transactional
  @DisplayName("Should delete multiple staff members for an entity")
  void deleteAllByEntityIdSuccess() {
    var city = factory.getAnyCity();
    var entity = factory.createEntity(city);

    var acc1 = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    var acc2 = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    factory.createStaff(acc1, entity);
    factory.createStaff(acc2, entity);

    long count = service.deleteAllByEntityId(entity.getId());

    assertThat(count).isEqualTo(2);
    verify(accountService).deleteAll(any());
  }

  @Test
  @DisplayName("Should return false when accountId is null on deactivate")
  void deactivateNullId() {
    assertThat(service.deactivate(null)).isFalse();
  }
}
