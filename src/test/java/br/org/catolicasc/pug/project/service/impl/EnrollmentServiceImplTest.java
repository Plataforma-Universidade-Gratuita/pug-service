package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.service.AuthService;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.EnrollmentCreateCommand;
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
@DisplayName("EnrollmentServiceImpl Coverage")
class EnrollmentServiceImplTest {

  @Inject EnrollmentServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuthService authService;

  private Student student;
  private Project project;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account studentAcc = factory.createAccount(factory.createUser(), AccountType.STUDENT);
    student = factory.createStudent(studentAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creatorAcc = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creatorAcc);

    doNothing().when(authService).requireCurrentAccountOfType(any());
    doNothing().when(authService).requireCurrentAccountNotOfType(any());
    when(authService.getCurrentAccountId()).thenReturn(studentAcc.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should save enrollment successfully")
  void saveSuccess() {
    EnrollmentCreateCommand cmd = new EnrollmentCreateCommand(project.getId());
    Enrollment saved = service.save(cmd);
    assertThat(saved.getStatus()).isEqualTo(EnrollmentStatus.PENDING);
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same enrollment")
  void saveDuplicate() {
    service.save(new EnrollmentCreateCommand(project.getId()));
    em.flush();
    em.clear();
    assertThrows(
        DuplicateResourceException.class,
        () -> service.save(new EnrollmentCreateCommand(project.getId())));
  }

  @Test
  @Transactional
  @DisplayName("Should accept enrollment")
  void acceptSuccess() {
    Enrollment enr = factory.createEnrollment(student, project);
    Enrollment accepted = service.accept(enr.getIdentifier());
    assertThat(accepted.getStatus()).isEqualTo(EnrollmentStatus.APPROVED);
  }

  @Test
  @Transactional
  @DisplayName("Should cancel approved enrollment")
  void cancelSuccess() {
    Enrollment enr =
        factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enr));
    em.flush();

    Enrollment canceled = service.cancel(enr.getIdentifier());
    assertThat(canceled.getStatus()).isEqualTo(EnrollmentStatus.CANCELED);
  }

  @Test
  @Transactional
  @DisplayName("Should complete approved enrollment")
  void completeSuccess() {
    Enrollment enr =
        factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enr));
    em.flush();

    Enrollment completed = service.complete(enr.getIdentifier());
    assertThat(completed.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
  }

  @Test
  @Transactional
  @DisplayName("Should exit approved enrollment")
  void exitSuccess() {
    Enrollment enr =
        factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enr));
    em.flush();

    Enrollment exited = service.exit(enr.getIdentifier());
    assertThat(exited.getStatus()).isEqualTo(EnrollmentStatus.EXITED);
  }

  @Test
  @Transactional
  @DisplayName("Should remove approved enrollment")
  void removeSuccess() {
    Enrollment enr =
        factory.createEnrollment(student, project).changeStatus(EnrollmentStatus.APPROVED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enr));
    em.flush();

    Enrollment removed = service.remove(enr.getIdentifier());
    assertThat(removed.getStatus()).isEqualTo(EnrollmentStatus.REMOVED);
  }

  @Test
  @Transactional
  @DisplayName("Should reject pending enrollment")
  void rejectSuccess() {
    Enrollment enr = factory.createEnrollment(student, project);
    Enrollment rejected = service.reject(enr.getIdentifier());
    assertThat(rejected.getStatus()).isEqualTo(EnrollmentStatus.REJECTED);
  }

  @Test
  @Transactional
  @DisplayName("Should fail to reject if transition is invalid")
  void rejectInvalid() {
    // Tentando rejeitar algo já completado
    Enrollment enr =
        factory
            .createEnrollment(student, project)
            .changeStatus(EnrollmentStatus.APPROVED)
            .changeStatus(EnrollmentStatus.COMPLETED);
    em.merge(br.org.catolicasc.pug.project.infra.EnrollmentMapper.toEntity(enr));
    em.flush();

    assertThrows(BusinessRuleException.class, () -> service.reject(enr.getIdentifier()));
  }

  @Test
  @Transactional
  @DisplayName("Should delete enrollment successfully")
  void deleteSuccess() {
    factory.createEnrollment(student, project);
    EnrollmentIdentifier id =
        EnrollmentIdentifier.builder()
            .projectId(project.getId())
            .studentId(student.getAccountId())
            .build();
    assertThat(service.delete(id)).isTrue();
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
    factory.createEnrollment(student, project);
    assertThat(service.existsAnyByStudentId(student.getAccountId())).isTrue();
    assertThat(service.existsAnyByProjectId(project.getId())).isTrue();
  }
}
