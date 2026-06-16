package br.org.catolicasc.pug.project.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.ProjectCreateCommandBuilder.aProjectCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.ProjectUpdateCommandBuilder.aProjectUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
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
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectServiceImpl Integration Tests")
class ProjectServiceImplTest {

  @Inject ProjectServiceImpl service;
  @Inject TestDataFactory factory;

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock EnrollmentsService enrollmentsService;

  private Entity partnerEntity;
  private Account creator;

  @BeforeEach
  void setup() {
    partnerEntity = factory.createEntity(factory.getAnyCity());
    creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);

    doNothing().when(authService).requireCurrentAccountNotOfType(any());
    doNothing().when(authService).requireCurrentAccountOfType(any());
    when(authService.getCurrentAccountId()).thenReturn(creator.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should save project successfully")
  void saveSuccess() {
    var cmd = aProjectCreateCommand().withEntityId(partnerEntity.getId()).build();

    Project saved = service.save(cmd);

    assertThat(saved.getName()).isEqualTo(cmd.name());
    assertThat(saved.getProjectStatus()).isEqualTo(ProjectStatus.PLANNED);
    verify(audit).fireCreate(Project.class.getName(), saved.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same name+entity")
  void saveDuplicate() {
    Project existing = factory.createProject(partnerEntity, creator);

    var cmd =
        aProjectCreateCommand()
            .withName(existing.getName())
            .withEntityId(partnerEntity.getId())
            .build();

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should update project successfully")
  void updateSuccess() {
    Project project = factory.createProject(partnerEntity, creator);
    var cmd = aProjectUpdateCommand().withDescription("Updated description").build();

    Project updated = service.update(project.getId(), cmd);

    assertThat(updated.getDescription()).isEqualTo("Updated description");
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Should throw when updating non-existing project")
  void updateNotFound() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    var cmd = aProjectUpdateCommand().withName("Name").build();

    assertThrows(ResourceNotFoundException.class, () -> service.update(id, cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete project successfully")
  void deleteSuccess() {
    Project project = factory.createProject(partnerEntity, creator);
    when(enrollmentsService.existsAnyByProjectId(project.getId())).thenReturn(false);

    boolean deleted = service.delete(project.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Project.class.getName(), project.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting project with enrollments")
  void deleteWithEnrollments() {
    Project project = factory.createProject(partnerEntity, creator);
    when(enrollmentsService.existsAnyByProjectId(project.getId())).thenReturn(true);

    assertThrows(BusinessRuleException.class, () -> service.delete(project.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting non-existing project")
  void deleteNonExisting() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(enrollmentsService.existsAnyByProjectId(id)).thenReturn(false);

    assertThat(service.delete(id)).isFalse();
  }

  @Test
  @DisplayName("Should throw when project not found by ID")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should get project by ID successfully")
  void getByIdSuccess() {
    Project project = factory.createProject(partnerEntity, creator);

    Project found = service.getById(project.getId());

    assertThat(found.getId()).isEqualTo(project.getId());
    assertThat(found.getName()).isEqualTo(project.getName());
  }

  @Test
  @DisplayName("Should check existence by entity ID")
  void existsAnyByEntityId() {
    factory.createProject(partnerEntity, creator);

    assertThat(service.existsAnyByEntityId(partnerEntity.getId())).isTrue();
  }

  @Test
  @DisplayName("Should return false for null entity ID")
  void existsAnyByEntityIdNull() {
    assertThat(service.existsAnyByEntityId(null)).isFalse();
  }

  @Test
  @DisplayName("Should check existence by created by")
  void existsByCreatedBy() {
    factory.createProject(partnerEntity, creator);

    assertThat(service.existsByCreatedBy(creator.getId())).isTrue();
  }

  @Test
  @DisplayName("Should return false for null created by")
  void existsByCreatedByNull() {
    assertThat(service.existsByCreatedBy(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should transition project to IN_PROGRESS")
  void transitionToInProgress() {
    Project project = factory.createProject(partnerEntity, creator);

    Project updated = service.transitionStatus(project.getId(), ProjectStatus.IN_PROGRESS);

    assertThat(updated.getProjectStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
  }

  @Test
  @Transactional
  @DisplayName("Should retake project from ON_HOLD to IN_PROGRESS")
  void transitionToInProgressFromOnHold() {
    Project project = factory.createProject(partnerEntity, creator);
    Project started = service.transitionStatus(project.getId(), ProjectStatus.IN_PROGRESS);
    Project onHold = service.transitionStatus(started.getId(), ProjectStatus.ON_HOLD);

    Project retaken = service.transitionStatus(onHold.getId(), ProjectStatus.IN_PROGRESS);

    assertThat(retaken.getProjectStatus()).isEqualTo(ProjectStatus.IN_PROGRESS);
    verify(enrollmentsService)
        .changeStatusByProjectId(
            eq(project.getId()), eq(EnrollmentStatus.ON_HOLD), eq(EnrollmentStatus.APPROVED));
  }

  @Test
  @Transactional
  @DisplayName("Should add completed hours")
  void addCompletedHours() {
    Project project = factory.createProject(partnerEntity, creator);

    Project updated = service.addCompletedHours(project.getId(), new BigDecimal("5.00"));

    assertThat(updated.getProjectInfo().getCompletedHours())
        .isEqualByComparingTo(new BigDecimal("5.00"));
  }

  @Test
  @Transactional
  @DisplayName("Should auto-complete project when hours exceed offered")
  void addCompletedHoursAutoCompletion() {
    Project project = factory.createProject(partnerEntity, creator);
    Project started = service.transitionStatus(project.getId(), ProjectStatus.IN_PROGRESS);

    Project updated = service.addCompletedHours(started.getId(), new BigDecimal("40.00"));

    assertThat(updated.getProjectStatus()).isEqualTo(ProjectStatus.COMPLETED);
    assertThat(updated.getProjectInfo().getCompletedHours()).isEqualByComparingTo("40.00");
    verify(enrollmentsService)
        .changeStatusByProjectId(eq(project.getId()), eq(EnrollmentStatus.COMPLETED));
  }

  @Test
  @Transactional
  @DisplayName("Should throw exception when adding hours to non-existent project")
  void addCompletedHoursNotFound() {
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.addCompletedHours(UuidCreator.getTimeOrderedEpoch(), BigDecimal.TEN));
  }

  @Test
  @DisplayName("Should return false for non-existing entity ID")
  void existsAnyByEntityIdFalse() {
    assertThat(service.existsAnyByEntityId(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @DisplayName("Should return false for non-existing created by")
  void existsByCreatedByFalse() {
    assertThat(service.existsByCreatedBy(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw exception when transition is invalid")
  void transitionStatusInvalid() {
    Project project = factory.createProject(partnerEntity, creator);
    assertThrows(
        BusinessRuleException.class,
        () -> service.transitionStatus(project.getId(), ProjectStatus.COMPLETED));
  }
}
