package br.org.catolicasc.pug.project.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AttendanceRepositoryImplTest {

  @Inject AttendanceRepositoryImpl repository;
  @Inject TestDataFactory factory;

  private FormerStudent formerStudent;
  private Project project;

  @BeforeEach
  void setup() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account sAcc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(sAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creator);

    factory.createEnrollment(formerStudent, project);
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Attendance")
  void shouldPersistAndFind() {
    Attendance attendance = factory.createAttendance(project, formerStudent);

    var found = repository.findOptionalById(attendance.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getQrValidationInfo().getQrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
  }

  @Test
  @Transactional
  @DisplayName("Should handle existsByQrHash and findOptionalByQrHash")
  void shouldHandleQrHashOperations() {
    Attendance attendance = factory.createAttendance(project, formerStudent);
    String hash = attendance.getQrValidationInfo().getQrValidationHash();

    assertThat(repository.existsByQrHash(hash)).isTrue();
    assertThat(repository.findOptionalByQrHash(hash)).isPresent();

    assertThat(repository.existsByQrHash("invalid-hash")).isFalse();
    assertThat(repository.findOptionalByQrHash(null)).isEmpty();
    assertThat(repository.existsByQrHash(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should update Attendance state")
  void shouldUpdateAttendance() {
    Account staffAccount = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Attendance attendance = factory.createAttendance(project, formerStudent);

    Attendance updated =
        attendance.validatePresence(staffAccount.getId(), AttendanceStatus.PRESENT);
    repository.update(updated);

    repository.flush();

    var found = repository.findOptionalById(attendance.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getStatus()).isEqualTo(AttendanceStatus.PRESENT);
    assertThat(found.get().getAttendanceInfo().getValidatedBy()).isEqualTo(staffAccount.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should delete only waiting attendances for the targeted enrollment")
  void shouldDeleteOnlyWaitingAttendancesForTargetEnrollment() {
    Attendance waitingTarget = factory.createAttendance(project, formerStudent);
    Account validator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    Attendance presentTarget =
        factory
            .createAttendance(project, formerStudent)
            .validatePresence(validator.getId(), AttendanceStatus.PRESENT);
    repository.update(presentTarget);
    repository.flush();

    AreaOfExpertise secondAreaOfExpertise = factory.createAreaOfExpertise();
    Course secondCourse = factory.createCourse(secondAreaOfExpertise);
    Account secondStudentAccount =
        factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    FormerStudent secondStudent = factory.createStudent(secondStudentAccount, secondCourse);
    factory.createEnrollment(secondStudent, project);
    Attendance waitingOtherEnrollment = factory.createAttendance(project, secondStudent);

    long deleted =
        repository.deleteAllWaitingValidationByEnrollmentId(
            project.getId(), formerStudent.getAccountId());

    assertThat(deleted).isEqualTo(1);
    assertThat(repository.findOptionalById(waitingTarget.getId())).isEmpty();
    assertThat(repository.findOptionalById(presentTarget.getId())).isPresent();
    assertThat(repository.findOptionalById(waitingOtherEnrollment.getId())).isPresent();
  }

  @Test
  @Transactional
  @DisplayName("Should handle edge cases for repository methods")
  void shouldHandleEdgeCases() {
    assertThat(repository.deleteById(null)).isFalse();

    assertThat(repository.deleteAllByEnrollmentId(null, null)).isZero();
    assertThat(repository.deleteAllWaitingValidationByEnrollmentId(null, null)).isZero();

    repository.update(null);

    assertThat(repository.existsByValidatedBy(null)).isFalse();
  }
}
