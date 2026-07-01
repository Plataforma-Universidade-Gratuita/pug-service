package br.org.catolicasc.pug.project.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.EnrollmentCreateCommandBuilder.anEnrollmentCreateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.FormerStudentsService;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.AttendancesService;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentsServiceImpl Integration Tests")
class EnrollmentsServiceImplTest {

  @Inject EnrollmentsServiceImpl service;
  @Inject TestDataFactory factory;

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock ProjectService projectService;
  @InjectMock FormerStudentsService studentService;
  @InjectMock ProjectAreaOfExpertiseService projectAreaOfExpertiseService;
  @InjectMock AttendancesService attendancesService;

  private AreaOfExpertise areaOfExpertise;
  private FormerStudent formerStudent;
  private Project project;
  private Enrollment enrollment;

  @BeforeEach
  void setup() {
    areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account studentAccount =
        factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(studentAccount, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creator);

    enrollment = factory.createEnrollment(formerStudent, project);

    when(authService.getCurrentAccountId()).thenReturn(formerStudent.getAccountId());
    when(projectService.getById(project.getId())).thenReturn(project);
    when(studentService.getById(formerStudent.getAccountId())).thenReturn(formerStudent);
    when(studentService.getAreaOfExpertise(formerStudent.getAccountId()))
        .thenReturn(areaOfExpertise);
    when(projectAreaOfExpertiseService.listByProjects(project.getId()))
        .thenReturn(List.of(areaOfExpertise));
  }

  @Test
  @Transactional
  @DisplayName("Should save enrollment successfully")
  void saveSuccess() {
    Project anotherProject =
        factory.createProject(
            factory.createEntity(factory.getAnyCity()),
            factory.createAccount(factory.createUser(), AccountType.PARTNER));
    when(projectService.getById(anotherProject.getId())).thenReturn(anotherProject);
    when(projectAreaOfExpertiseService.listByProjects(anotherProject.getId()))
        .thenReturn(List.of(areaOfExpertise));

    EnrollmentCreateCommand cmd =
        anEnrollmentCreateCommand()
            .withProjectId(anotherProject.getId())
            .withStudentId(formerStudent.getAccountId())
            .build();

    Enrollment saved = service.save(cmd);

    assertThat(saved.getIdentifier().getProjectId()).isEqualTo(anotherProject.getId());
    assertThat(saved.getIdentifier().getFormerStudentId()).isEqualTo(formerStudent.getAccountId());
    assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
    verify(audit).fireCreate(Enrollment.class.getName(), anotherProject.getId());
  }

  @Test
  @Transactional
  @DisplayName(
      "Should save enrollment using current former-student account when command omits student")
  void saveUsesCurrentAccountWhenFormerStudentIdIsNull() {
    Project anotherProject =
        factory.createProject(
            factory.createEntity(factory.getAnyCity()),
            factory.createAccount(factory.createUser(), AccountType.PARTNER));
    when(projectService.getById(anotherProject.getId())).thenReturn(anotherProject);
    when(projectAreaOfExpertiseService.listByProjects(anotherProject.getId()))
        .thenReturn(List.of(areaOfExpertise));

    Enrollment saved = service.save(new EnrollmentCreateCommand(anotherProject.getId(), null));

    assertThat(saved.getIdentifier().getProjectId()).isEqualTo(anotherProject.getId());
    assertThat(saved.getIdentifier().getFormerStudentId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException when enrollment already exists")
  void saveDuplicate() {
    var cmd =
        anEnrollmentCreateCommand()
            .withProjectId(project.getId())
            .withStudentId(formerStudent.getAccountId())
            .build();

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should throw when former student and project do not share area of expertise")
  void saveAreaOfExpertiseMismatch() {
    AreaOfExpertise anotherAreaOfExpertise = factory.createAreaOfExpertise();
    Project anotherProject =
        factory.createProject(
            factory.createEntity(factory.getAnyCity()),
            factory.createAccount(factory.createUser(), AccountType.PARTNER));
    when(projectService.getById(anotherProject.getId())).thenReturn(anotherProject);
    when(projectAreaOfExpertiseService.listByProjects(anotherProject.getId()))
        .thenReturn(List.of(anotherAreaOfExpertise));

    EnrollmentCreateCommand cmd =
        anEnrollmentCreateCommand()
            .withProjectId(anotherProject.getId())
            .withStudentId(formerStudent.getAccountId())
            .build();

    assertThrows(BusinessRuleException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should throw when creating enrollment for canceled project")
  void saveCanceledProject() {
    Project canceledProject = project.cancel();
    when(projectService.getById(canceledProject.getId())).thenReturn(canceledProject);
    when(projectAreaOfExpertiseService.listByProjects(canceledProject.getId()))
        .thenReturn(List.of(areaOfExpertise));

    EnrollmentCreateCommand cmd =
        anEnrollmentCreateCommand()
            .withProjectId(canceledProject.getId())
            .withStudentId(formerStudent.getAccountId())
            .build();

    assertThrows(BusinessRuleException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName(
      "Should throw when creating enrollment for former student with concluded counterpart")
  void saveFormerStudentWithConcludedCounterpart() {
    FormerStudent concludedFormerStudent =
        formerStudent.addCompletedHours(formerStudent.getCounterpartHours().getRequiredHours());
    when(studentService.getById(formerStudent.getAccountId())).thenReturn(concludedFormerStudent);

    EnrollmentCreateCommand cmd =
        anEnrollmentCreateCommand()
            .withProjectId(project.getId())
            .withStudentId(formerStudent.getAccountId())
            .build();

    assertThrows(BusinessRuleException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should transition enrollment status")
  void changeStatusSuccess() {
    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should delete waiting attendances when enrollment is canceled")
  void changeStatusCanceledDeletesWaitingAttendances() {
    Enrollment approved =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    Enrollment updated = service.changeStatus(approved.getIdentifier(), EnrollmentStatus.CANCELED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
    verify(attendancesService)
        .deleteAllWaitingValidationByEnrollmentIdentifier(approved.getIdentifier());
  }

  @Test
  @Transactional
  @DisplayName("Should delete waiting attendances when enrollment is completed")
  void changeStatusCompletedDeletesWaitingAttendances() {
    Enrollment approved =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    Enrollment updated = service.changeStatus(approved.getIdentifier(), EnrollmentStatus.COMPLETED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    verify(attendancesService)
        .deleteAllWaitingValidationByEnrollmentIdentifier(approved.getIdentifier());
  }

  @Test
  @Transactional
  @DisplayName("Should place approved pending enrollment on hold when project is on hold")
  void changeStatusApprovedOnOnHoldProjectBecomesOnHold() {
    Project onHoldProject = project.start().putOnHold();
    when(projectService.getById(project.getId())).thenReturn(onHoldProject);

    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.ON_HOLD);
    assertThat(onHoldProject.getProjectStatus()).isEqualTo(ProjectStatus.ON_HOLD);
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should return same enrollment when transitioning to same status")
  void changeStatusToSameStatusIsIdempotent() {
    Enrollment updated = service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.PENDING);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
  }

  @Test
  @Transactional
  @DisplayName("Should throw when transition is invalid")
  void changeStatusInvalid() {
    assertThrows(
        BusinessRuleException.class,
        () -> service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.COMPLETED));
  }

  @Test
  @DisplayName("Should return zero for null inputs in bulk project status transition")
  void changeStatusByProjectIdNullInputs() {
    assertThat(service.changeStatusByProjectId(null, EnrollmentStatus.APPROVED)).isZero();
    assertThat(service.changeStatusByProjectId(project.getId(), null)).isZero();

    assertThat(
            service.changeStatusByProjectId(
                null, EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED))
        .isZero();
    assertThat(service.changeStatusByProjectId(project.getId(), null, EnrollmentStatus.APPROVED))
        .isZero();
    assertThat(service.changeStatusByProjectId(project.getId(), EnrollmentStatus.PENDING, null))
        .isZero();
  }

  @Test
  @Transactional
  @DisplayName("Should change all eligible enrollments for a project")
  void changeStatusByProjectIdSuccess() {
    long changed = service.changeStatusByProjectId(project.getId(), EnrollmentStatus.APPROVED);

    assertThat(changed).isGreaterThanOrEqualTo(1);
    assertThat(service.getByIds(enrollment.getIdentifier()).getStatus())
        .isEqualTo(EnrollmentStatus.APPROVED);
  }

  @Test
  @Transactional
  @DisplayName("Should skip enrollments whose lifecycle does not allow the bulk transition")
  void changeStatusByProjectIdSkipsInvalidTransitions() {
    long changed = service.changeStatusByProjectId(project.getId(), EnrollmentStatus.COMPLETED);

    assertThat(changed).isZero();
  }

  @Test
  @Transactional
  @DisplayName("Should change only enrollments matching current status")
  void changeStatusByProjectIdFilteringCurrentStatus() {
    long changed =
        service.changeStatusByProjectId(
            project.getId(), EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED);

    assertThat(changed).isGreaterThanOrEqualTo(1);
    assertThat(service.getByIds(enrollment.getIdentifier()).getStatus())
        .isEqualTo(EnrollmentStatus.APPROVED);
  }

  @Test
  @Transactional
  @DisplayName("Should ignore enrollments that do not match current status")
  void changeStatusByProjectIdIgnoresDifferentStatus() {
    long changed =
        service.changeStatusByProjectId(
            project.getId(), EnrollmentStatus.APPROVED, EnrollmentStatus.COMPLETED);

    assertThat(changed).isZero();
    assertThat(service.getByIds(enrollment.getIdentifier()).getStatus())
        .isEqualTo(EnrollmentStatus.PENDING);
  }

  @Test
  @DisplayName("Should return zero when completing enrollments for null former-student ID")
  void completeAllByFormerStudentIdNull() {
    assertThat(service.completeAllByFormerStudentId(null)).isZero();
  }

  @Test
  @Transactional
  @DisplayName("Should complete all eligible enrollments for former student")
  void completeAllByFormerStudentIdSuccess() {
    service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    long completed = service.completeAllByFormerStudentId(formerStudent.getAccountId());

    assertThat(completed).isGreaterThanOrEqualTo(1);
    assertThat(service.getByIds(enrollment.getIdentifier()).getStatus())
        .isEqualTo(EnrollmentStatus.COMPLETED);
  }

  @Test
  @Transactional
  @DisplayName("Should skip invalid former-student enrollments when completing all")
  void completeAllByFormerStudentIdSkipsInvalidTransitions() {
    long completed = service.completeAllByFormerStudentId(formerStudent.getAccountId());

    assertThat(completed).isZero();
  }

  @Test
  @Transactional
  @DisplayName("Should delete enrollment")
  void deleteSuccess() {
    boolean deleted = service.delete(enrollment.getIdentifier());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Enrollment.class.getName(), enrollment.getIdentifier().getProjectId());
  }

  @Test
  @DisplayName("Should return false when deleting null identifier")
  void deleteNullIdentifier() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @DisplayName("Should check existence by project and former-student IDs")
  void existsChecks() {
    assertThat(service.existsAnyByProjectId(project.getId())).isTrue();
    assertThat(service.existsAnyByProjectId(null)).isFalse();

    assertThat(service.existsAnyByFormerStudentId(formerStudent.getAccountId())).isTrue();
    assertThat(service.existsAnyByFormerStudentId(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should get enrollment by composite identifier")
  void getByIdsSuccess() {
    Enrollment found = service.getByIds(enrollment.getIdentifier());

    assertThat(found.getIdentifier()).isEqualTo(enrollment.getIdentifier());
  }

  @Test
  @DisplayName("Should throw when enrollment is not found")
  void getByIdsNotFound() {
    EnrollmentIdentifier missing =
        EnrollmentIdentifier.factory(
            UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());

    assertThrows(ResourceNotFoundException.class, () -> service.getByIds(missing));
  }
}
