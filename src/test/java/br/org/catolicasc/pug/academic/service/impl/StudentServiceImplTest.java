package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.StudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.project.service.EnrollmentService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StudentServiceImpl Integration Tests")
class StudentServiceImplTest {

  @Inject StudentServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;
  @InjectMock EnrollmentService enrollmentService;

  @Test
  @Transactional
  @DisplayName("Should save student successfully")
  void saveSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    StudentCreateCommand cmd = buildCreateCommand(course.getId());
    Student saved = service.save(cmd);

    assertThat(saved.getAccountId()).isNotNull();
    assertThat(saved.getCourseId()).isEqualTo(course.getId());
    verify(audit).fireCreate(Student.class.getName(), saved.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when course not found on save")
  void saveCourseNotFound() {
    StudentCreateCommand cmd = buildCreateCommand(UUID.randomUUID());
    assertThrows(ResourceNotFoundException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same registration")
  void saveDuplicateRegistration() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    Student existingStudent = service.getById(account.getId());

    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            cpf + "@test.com",
            AccountType.STUDENT,
            "hash",
            new UserCreateCommand(cpf, "Another Student"));
    StudentCreateCommand cmd =
        new StudentCreateCommand(
            accCmd,
            existingStudent.getAcademicRegistration().getValue(),
            Campi.JOINVILLE,
            course.getId(),
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should get student by ID")
  void getByIdSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    Student found = service.getById(account.getId());
    assertThat(found.getAccountId()).isEqualTo(account.getId());
  }

  @Test
  @DisplayName("Should throw when student not found")
  void getByIdNotFound() {
    assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
  }

  @Test
  @Transactional
  @DisplayName("Should update student successfully")
  void updateSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    StudentUpdateCommand cmd =
        new StudentUpdateCommand(null, null, Campi.JOINVILLE, null, null, null, null);
    Student updated = service.update(account.getId(), cmd);

    assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
  }

  @Test
  @DisplayName("Should throw when updating non-existing student")
  void updateNotFound() {
    StudentUpdateCommand cmd = new StudentUpdateCommand(null, null, null, null, null, null, null);
    assertThrows(ResourceNotFoundException.class, () -> service.update(UUID.randomUUID(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete student successfully")
  void deleteSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    when(enrollmentService.existsAnyByStudentId(account.getId())).thenReturn(false);
    boolean deleted = service.delete(account.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Student.class.getName(), account.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting student with enrollments")
  void deleteWithEnrollments() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    when(enrollmentService.existsAnyByStudentId(account.getId())).thenReturn(true);
    assertThrows(BusinessRuleException.class, () -> service.delete(account.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should add completed hours successfully")
  void addCompletedHoursSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    Student updated = service.addCompletedHours(account.getId(), new BigDecimal("10"));
    assertThat(updated.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(new BigDecimal("10"));
  }

  @Test
  @DisplayName("Should throw when adding hours to non-existing student")
  void addCompletedHoursNotFound() {
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.addCompletedHours(UUID.randomUUID(), new BigDecimal("10")));
  }

  @Test
  @Transactional
  @DisplayName("Should save in bulk successfully")
  void saveInBulkSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    StudentCreateCommand cmd1 = buildCreateCommand(course.getId());
    StudentCreateCommand cmd2 = buildCreateCommand(course.getId());

    List<Student> students = service.saveInBulk(List.of(cmd1, cmd2));
    assertThat(students).hasSize(2);
  }

  @Test
  @DisplayName("Should return empty list for empty bulk")
  void saveInBulkEmpty() {
    assertThat(service.saveInBulk(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should delegate existsAnyByCourseId to repo")
  void existsAnyByCourseId() {
    assertThat(service.existsAnyByCourseId(UUID.randomUUID())).isFalse();
  }

  /* --- helpers --- */

  private StudentCreateCommand buildCreateCommand(UUID courseId) {
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            cpf + "@test.com",
            AccountType.STUDENT,
            "hash",
            new UserCreateCommand(cpf, "Test Student " + cpf));
    String reg = UUID.randomUUID().toString().substring(0, 14).toUpperCase();
    return new StudentCreateCommand(
        accCmd,
        reg,
        Campi.JOINVILLE,
        courseId,
        new BigDecimal("100"),
        LocalDate.now(),
        LocalDate.now().plusMonths(6));
  }
}
