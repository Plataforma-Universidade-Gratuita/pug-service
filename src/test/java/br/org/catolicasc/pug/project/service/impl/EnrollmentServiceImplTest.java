package br.org.catolicasc.pug.project.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.EnrollmentCreateCommandBuilder.anEnrollmentCreateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EnrollmentsServiceImpl Coverage")
class EnrollmentServiceImplTest {

  @Inject EnrollmentsServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  private FormerStudent formerStudent;
  private Project project;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account studentAcc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(studentAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creatorAcc = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creatorAcc);

    doNothing().when(authService).requireCurrentAccountOfType(any());
    doNothing().when(authService).requireCurrentAccountNotOfType(any());
    when(authService.getCurrentAccountId()).thenReturn(studentAcc.getId());
    when(authService.getCurrentAccountType()).thenReturn(AccountType.FORMER_STUDENT);
  }

  @Test
  @Transactional
  @DisplayName("Should save enrollment successfully")
  void saveSuccess() {
    Enrollment saved =
        service.save(anEnrollmentCreateCommand().withProjectId(project.getId()).build());

    assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same enrollment")
  void saveDuplicate() {
    service.save(anEnrollmentCreateCommand().withProjectId(project.getId()).build());
    em.flush();
    em.clear();

    assertThrows(
        DuplicateResourceException.class,
        () -> service.save(anEnrollmentCreateCommand().withProjectId(project.getId()).build()));
  }

  @Test
  @Transactional
  @DisplayName("Should allow admin to create enrollment for a given student")
  void saveAsAdminForStudentSuccess() {
    when(authService.getCurrentAccountType()).thenReturn(AccountType.ADMIN);

    Enrollment saved =
        service.save(
            anEnrollmentCreateCommand()
                .withProjectId(project.getId())
                .withStudentId(formerStudent.getAccountId())
                .build());

    assertThat(saved.getIdentifier().getStudentId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should change enrollment status to approved")
  void changeStatusToApprovedSuccess() {
    Enrollment enrollment = factory.createEnrollment(formerStudent, project);

    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.APPROVED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
  }

  @Test
  @Transactional
  @DisplayName("Should change approved enrollment status to canceled")
  void changeStatusToCanceledSuccess() {
    Enrollment enrollment =
        factory.createEnrollment(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enrollment));
    em.flush();

    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.CANCELED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
  }

  @Test
  @Transactional
  @DisplayName("Should change approved enrollment status to completed")
  void changeStatusToCompletedSuccess() {
    Enrollment enrollment =
        factory.createEnrollment(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enrollment));
    em.flush();

    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.COMPLETED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
  }

  @Test
  @Transactional
  @DisplayName("Should change approved enrollment status to exited")
  void changeStatusToExitedSuccess() {
    Enrollment enrollment =
        factory.createEnrollment(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enrollment));
    em.flush();

    Enrollment updated = service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.EXITED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.EXITED);
  }

  @Test
  @Transactional
  @DisplayName("Should change approved enrollment status to removed")
  void changeStatusToRemovedSuccess() {
    Enrollment enrollment =
        factory.createEnrollment(formerStudent, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enrollment));
    em.flush();

    Enrollment updated = service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.REMOVED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.REMOVED);
  }

  @Test
  @Transactional
  @DisplayName("Should change pending enrollment status to rejected")
  void changeStatusToRejectedSuccess() {
    Enrollment enrollment = factory.createEnrollment(formerStudent, project);

    Enrollment updated =
        service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.REJECTED);

    assertThat(updated.getStatus()).isEqualTo(EnrollmentStatus.REJECTED);
  }

  @Test
  @Transactional
  @DisplayName("Should fail when transition is invalid")
  void changeStatusInvalid() {
    Enrollment enrollment =
        factory
            .createEnrollment(formerStudent, project)
            .changeStatus(EnrollmentStatus.APPROVED)
            .changeStatus(EnrollmentStatus.COMPLETED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enrollment));
    em.flush();

    assertThrows(
        BusinessRuleException.class,
        () -> service.changeStatus(enrollment.getIdentifier(), EnrollmentStatus.REJECTED));
  }

  @Test
  @Transactional
  @DisplayName("Should delete enrollment successfully")
  void deleteSuccess() {
    factory.createEnrollment(formerStudent, project);
    EnrollmentIdentifier identifier =
        EnrollmentIdentifier.builder()
            .projectId(project.getId())
            .studentId(formerStudent.getAccountId())
            .build();

    assertThat(service.delete(identifier)).isTrue();
  }

  @Test
  @DisplayName("Should return false for nulls")
  void nullChecks() {
    assertThat(service.delete(null)).isFalse();
    assertThat(service.existsAnyByStudentId(null)).isFalse();
    assertThat(service.existsAnyByProjectId(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should check existence")
  void exists() {
    factory.createEnrollment(formerStudent, project);

    assertThat(service.existsAnyByStudentId(formerStudent.getAccountId())).isTrue();
    assertThat(service.existsAnyByProjectId(project.getId())).isTrue();
  }
}
