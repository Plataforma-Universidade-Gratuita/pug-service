package br.org.catolicasc.pug.project.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.AttendanceCreateCommandBuilder.anAttendanceCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.AttendanceValidateCommandBuilder.anAttendanceValidateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.service.StudentService;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.ProjectService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
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
@DisplayName("AttendanceServiceImpl Integration Tests")
class AttendanceServiceImplTest {

  @Inject AttendanceServiceImpl service;
  @Inject TestDataFactory factory;

  @InjectMock AuditPublisher audit;
  @InjectMock AuthService authService;
  @InjectMock StudentService studentService;
  @InjectMock ProjectService projectService;

  private Student student;
  private Project project;
  private Attendance attendance;
  private Account creatorAcc;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account studentAcc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    student = factory.createStudent(studentAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    creatorAcc = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creatorAcc);

    factory.createEnrollment(student, project);
    attendance = factory.createAttendance(project, student);

    doNothing().when(authService).requireCurrentAccountNotOfType(any());
    doNothing().when(authService).requireCurrentAccountOfType(any());
    when(authService.getCurrentAccountId()).thenReturn(creatorAcc.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should save attendance successfully")
  void saveSuccess() {
    when(projectService.getById(project.getId())).thenReturn(project);
    when(studentService.getById(student.getAccountId())).thenReturn(student);

    var cmd =
        anAttendanceCreateCommand()
            .withProjectId(project.getId())
            .withStudentId(student.getAccountId())
            .build();
    Attendance saved = service.save(cmd);

    assertThat(saved).isNotNull();
    assertThat(saved.getStatus()).isEqualTo(AttendanceStatus.WAITING);
    verify(audit).fireCreate(any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should validate attendance successfully")
  void validateSuccess() {
    var cmd =
        anAttendanceValidateCommand()
            .withQrValidationHash(attendance.getQrValidationInfo().getQrValidationHash())
            .withStatus(AttendanceStatus.PRESENT)
            .build();

    Attendance validated = service.validate(attendance.getId(), cmd);

    assertThat(validated.getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    verify(studentService).addCompletedHours(any(), any());
    verify(projectService).addCompletedHours(any(), any());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @DisplayName("Should fail validation on wrong hash")
  void validateWrongHash() {
    var cmd = anAttendanceValidateCommand().withQrValidationHash("wrong-hash").build();
    assertThrows(ResourceNotFoundException.class, () -> service.validate(attendance.getId(), cmd));
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
    boolean deleted = service.delete(attendance.getId());
    assertThat(deleted).isTrue();
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
            .studentId(student.getAccountId())
            .build();
    long deleted = service.deleteAllByEnrollmentIdentifier(identifier);
    assertThat(deleted).isGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should return correct existence checks")
  void existsChecks() {
    assertThat(service.existsByValidatedBy(null)).isFalse();
    assertThat(service.existsByValidatedBy(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }
}
