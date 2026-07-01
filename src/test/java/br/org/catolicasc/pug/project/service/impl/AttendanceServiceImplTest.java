package br.org.catolicasc.pug.project.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.AttendanceCreateCommandBuilder.anAttendanceCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.AttendanceValidateCommandBuilder.anAttendanceValidateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import br.org.catolicasc.pug.partner.service.StaffService;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.EnrollmentRepository;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancesServiceImpl Integration Tests")
class AttendanceServiceImplTest {

  @Inject AttendancesServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EnrollmentRepository enrollmentRepository;

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock FormerStudentsService studentService;
  @InjectMock ProjectService projectService;
  @InjectMock StaffService staffService;

  private FormerStudent formerStudent;
  private Project project;
  private Attendance attendance;
  private Account creatorAcc;
  private Entity entity;

  @BeforeEach
  void setup() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account studentAcc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(studentAcc, course);

    entity = factory.createEntity(factory.getAnyCity());
    creatorAcc = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creatorAcc);

    factory.createApprovedEnrollment(formerStudent, project);
    attendance = factory.createAttendance(project, formerStudent);

    doNothing().when(authService).requireCurrentAccountNotOfType(any());
    doNothing().when(authService).requireCurrentAccountOfType(any());
    when(authService.getCurrentAccountType()).thenReturn(AccountType.FORMER_STUDENT);
    when(authService.getCurrentAccountId()).thenReturn(formerStudent.getAccountId());
    when(studentService.getById(any())).thenReturn(formerStudent);
    when(projectService.getById(any())).thenReturn(project);
  }

  @Test
  @Transactional
  @DisplayName("Should save attendance successfully")
  void saveSuccess() {
    Attendance saved =
        service.save(
            anAttendanceCreateCommand()
                .withProjectId(project.getId())
                .withStudentId(formerStudent.getAccountId())
                .build());

    assertThat(saved).isNotNull();
    assertThat(saved.getStatus()).isEqualTo(AttendanceStatus.WAITING);
    verify(audit).fireCreate(any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should fail to save attendance when enrollment does not exist")
  void saveFailsWhenEnrollmentDoesNotExist() {
    BusinessRuleException ex =
        assertThrows(
            BusinessRuleException.class,
            () ->
                service.save(
                    anAttendanceCreateCommand()
                        .withProjectId(project.getId())
                        .withStudentId(UuidCreator.getTimeOrderedEpoch())
                        .build()));

    assertThat(ex.getCode()).isEqualTo(ProjectsErrorCodes.ATTENDANCE_ENROLLMENT_NOT_FOUND);
  }

  @Test
  @Transactional
  @DisplayName("Should fail to save attendance when enrollment is not approved")
  void saveFailsWhenEnrollmentIsNotApproved() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account pendingStudentAcc =
        factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    FormerStudent pendingStudent = factory.createStudent(pendingStudentAcc, course);
    Entity pendingEntity = factory.createEntity(factory.getAnyCity());
    Account pendingCreator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Project pendingProject = factory.createProject(pendingEntity, pendingCreator);
    Enrollment pendingEnrollment = Enrollment.factory(pendingStudent, pendingProject);
    enrollmentRepository.persist(pendingEnrollment);
    when(authService.getCurrentAccountId()).thenReturn(pendingStudent.getAccountId());

    BusinessRuleException ex =
        assertThrows(
            BusinessRuleException.class,
            () ->
                service.save(
                    anAttendanceCreateCommand()
                        .withProjectId(pendingProject.getId())
                        .withStudentId(pendingStudent.getAccountId())
                        .build()));

    assertThat(ex.getCode()).isEqualTo(ProjectsErrorCodes.ATTENDANCE_ENROLLMENT_NOT_APPROVED);
  }

  @Test
  @Transactional
  @DisplayName("Should validate attendance successfully")
  void validateSuccess() {
    when(authService.getCurrentAccountType()).thenReturn(AccountType.PARTNER);
    when(authService.getCurrentAccountId()).thenReturn(creatorAcc.getId());
    when(staffService.getByAccountId(creatorAcc.getId()))
        .thenReturn(factory.createStaff(creatorAcc, entity));
    doNothing().when(projectService).validateIsInProgress(project.getId());

    Attendance validated =
        service.validate(
            attendance.getId(),
            anAttendanceValidateCommand()
                .withQrValidationHash(attendance.getQrValidationInfo().getQrValidationHash())
                .withStatus(AttendanceStatus.PRESENT)
                .build());

    assertThat(validated.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    verify(studentService).addCompletedHours(any(), any());
    verify(projectService).addCompletedHours(any(), any());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should fail to validate attendance as present when project is not in progress")
  void validatePresentFailsWhenProjectIsNotInProgress() {
    when(authService.getCurrentAccountType()).thenReturn(AccountType.PARTNER);
    when(authService.getCurrentAccountId()).thenReturn(creatorAcc.getId());
    when(staffService.getByAccountId(creatorAcc.getId()))
        .thenReturn(factory.createStaff(creatorAcc, entity));
    doThrow(new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_PROJECT_NOT_IN_PROGRESS))
        .when(projectService)
        .validateIsInProgress(project.getId());

    BusinessRuleException ex =
        assertThrows(
            BusinessRuleException.class,
            () ->
                service.validate(
                    attendance.getId(),
                    anAttendanceValidateCommand()
                        .withQrValidationHash(
                            attendance.getQrValidationInfo().getQrValidationHash())
                        .withStatus(AttendanceStatus.PRESENT)
                        .build()));

    assertThat(ex.getCode()).isEqualTo(ProjectsErrorCodes.ATTENDANCE_PROJECT_NOT_IN_PROGRESS);
  }

  @Test
  @Transactional
  @DisplayName("Should remove hours when attendance changes from PRESENT to ABSENT")
  void validatePresentToAbsentRemovesHours() {
    when(authService.getCurrentAccountType()).thenReturn(AccountType.PARTNER);
    when(authService.getCurrentAccountId()).thenReturn(creatorAcc.getId());
    when(staffService.getByAccountId(creatorAcc.getId()))
        .thenReturn(factory.createStaff(creatorAcc, entity));
    doNothing().when(projectService).validateIsInProgress(project.getId());
    FormerStudent progressedFormerStudent =
        formerStudent.addCompletedHours(attendance.getQrValidationInfo().getDuration());
    Project progressedProject =
        project.addCompletedHours(attendance.getQrValidationInfo().getDuration());
    when(studentService.getById(any())).thenReturn(formerStudent, progressedFormerStudent);
    when(projectService.getById(any())).thenReturn(project, progressedProject);

    Attendance presentAttendance =
        service.validate(
            attendance.getId(),
            anAttendanceValidateCommand()
                .withQrValidationHash(attendance.getQrValidationInfo().getQrValidationHash())
                .withStatus(AttendanceStatus.PRESENT)
                .build());

    Attendance absentAttendance =
        service.validate(
            presentAttendance.getId(),
            anAttendanceValidateCommand()
                .withQrValidationHash(presentAttendance.getQrValidationInfo().getQrValidationHash())
                .withStatus(AttendanceStatus.ABSENT)
                .build());

    assertThat(absentAttendance.getStatus()).isEqualTo(AttendanceStatus.ABSENT);
    verify(studentService).removeCompletedHours(any(), any());
    verify(projectService).removeCompletedHours(any(), any());
  }

  @Test
  @DisplayName("Should fail validation on wrong hash")
  void validateWrongHash() {
    assertThrows(
        ResourceNotFoundException.class,
        () ->
            service.validate(
                attendance.getId(),
                anAttendanceValidateCommand().withQrValidationHash("wrong-hash").build()));
  }

  @Test
  @Transactional
  @DisplayName("Should get attendance by ID")
  void getByIdSuccess() {
    Attendance found = service.getById(attendance.getId());
    assertThat(found.getId()).isEqualTo(attendance.getId());
  }

  @Test
  @DisplayName("Should throw when attendance not found")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should delete attendance successfully")
  void deleteSuccess() {
    assertThat(service.delete(attendance.getId())).isTrue();
  }

  @Test
  @DisplayName("Should return false for non-existing delete")
  void deleteNonExisting() {
    assertThat(service.delete(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should delete all by enrollment identifier")
  void deleteAllByEnrollmentIdentifier() {
    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder()
            .projectId(project.getId())
            .formerStudentId(formerStudent.getAccountId())
            .build();

    assertThat(service.deleteAllByEnrollmentIdentifier(identifier)).isGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should return correct existence checks")
  void existsChecks() {
    assertThat(service.existsByValidatedBy(null)).isFalse();
    assertThat(service.existsByValidatedBy(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }
}
