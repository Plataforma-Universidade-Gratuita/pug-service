package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentUpdateCommand;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.service.AccountsService;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountUpdateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserUpdateCommand;
import br.org.catolicasc.pug.project.service.EnrollmentsService;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentsServiceImpl Integration Tests")
class FormerStudentsServiceImplTest {

  @Inject FormerStudentsServiceImpl service;
  @Inject TestDataFactory factory;

  @InjectMock AuditPublisher auditPublisher;
  @InjectMock AccountsService accountService;
  @InjectMock CoursesService courseService;
  @InjectMock EnrollmentsService enrollmentsService;

  private Course course;
  private FormerStudent formerStudent;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    Account account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    course = factory.createCourse(areaOfExpertise);
    formerStudent = factory.createStudent(account, course);

    when(courseService.getById(course.getId())).thenReturn(course);
    when(accountService.save(any())).thenReturn(account);
    when(accountService.saveInBulk(any())).thenReturn(List.of(account));
    when(enrollmentsService.existsAnyByFormerStudentId(any())).thenReturn(false);
  }

  @Test
  @Transactional
  @DisplayName("Should get former student by ID")
  void getByIdSuccess() {
    assertThat(service.getById(formerStudent.getAccountId()).getAccountId())
        .isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @DisplayName("Should throw when former student is not found")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should save former student")
  void saveSuccess() {
    Account newAccount = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    when(accountService.save(any())).thenReturn(newAccount);

    FormerStudentCreateCommand cmd =
        createCommand("REG" + UuidCreator.getTimeOrderedEpoch().toString().substring(0, 8));

    FormerStudent saved = service.save(cmd);

    assertThat(saved.getAccountId()).isEqualTo(newAccount.getId());
    assertThat(saved.getAcademicRegistration().getValue()).isEqualTo(cmd.academicRegistration());
    verify(auditPublisher).fireCreate(FormerStudent.class.getName(), newAccount.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when saving duplicate registration")
  void saveDuplicateRegistration() {
    Account newAccount = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    when(accountService.save(any())).thenReturn(newAccount);

    FormerStudentCreateCommand cmd =
        createCommand(formerStudent.getAcademicRegistration().getValue());

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception when saving invalid data")
  void saveInvalidData() {
    Account newAccount = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    when(accountService.save(any())).thenReturn(newAccount);

    FormerStudentCreateCommand cmd =
        new FormerStudentCreateCommand(accountCreateCommand(), "", null, null, null, null, null);

    assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should bulk save former students")
  void saveInBulkSuccess() {
    Account first = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    Account second = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    when(accountService.saveInBulk(any())).thenReturn(List.of(first, second));

    var cmd1 = createCommand("BULK001");
    var cmd2 = createCommand("BULK002");

    var saved = service.saveInBulk(List.of(cmd1, cmd2));

    assertThat(saved).hasSize(2);
    assertThat(saved)
        .extracting(s -> s.getAcademicRegistration().getValue())
        .containsExactly("BULK001", "BULK002");
  }

  @Test
  @DisplayName("Should return empty list when bulk saving null or empty input")
  void saveInBulkEmptyInputs() {
    assertThat(service.saveInBulk(null)).isEmpty();
    assertThat(service.saveInBulk(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when bulk payload has duplicate registrations")
  void saveInBulkDuplicateRegistrationsInPayload() {
    var cmd1 = createCommand("DUP001");
    var cmd2 = createCommand("DUP001");

    assertThrows(DuplicateResourceException.class, () -> service.saveInBulk(List.of(cmd1, cmd2)));
  }

  @Test
  @Transactional
  @DisplayName("Should update former student")
  void updateSuccess() {
    UUID newCourseId = course.getId();
    FormerStudentUpdateCommand cmd =
        new FormerStudentUpdateCommand(
            new AccountUpdateCommand(
                "updated@example.com", null, null, new UserUpdateCommand("Updated Name")),
            "REGUPDATED",
            Campi.JOINVILLE,
            newCourseId,
            new BigDecimal("200"),
            LocalDate.now(),
            LocalDate.now().plusMonths(12));

    FormerStudent updated = service.update(formerStudent.getAccountId(), cmd);

    assertThat(updated.getAcademicRegistration().getValue()).isEqualTo("REGUPDATED");
    assertThat(updated.getCounterpartHours().getRequiredHours())
        .isEqualByComparingTo(new BigDecimal("200"));
    verify(accountService).update(any(), any());
    verify(auditPublisher).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when update tries to use an existing registration")
  void updateDuplicateRegistration() {
    Account secondAccount = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    FormerStudent second = factory.createStudent(secondAccount, course);

    FormerStudentUpdateCommand cmd =
        new FormerStudentUpdateCommand(
            null, second.getAcademicRegistration().getValue(), null, null, null, null, null);

    assertThrows(
        DuplicateResourceException.class, () -> service.update(formerStudent.getAccountId(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should update account status")
  void updateStatusSuccess() {
    FormerStudent updated = service.updateStatus(formerStudent.getAccountId(), false);

    assertThat(updated.getAccountId()).isEqualTo(formerStudent.getAccountId());
    verify(accountService).update(any(), any());
    verify(auditPublisher).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should add completed hours")
  void addCompletedHoursSuccess() {
    FormerStudent updated =
        service.addCompletedHours(formerStudent.getAccountId(), new BigDecimal("20"));

    assertThat(updated.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(new BigDecimal("20"));
  }

  @Test
  @Transactional
  @DisplayName("Should complete all enrollments when hours reach requirement")
  void addCompletedHoursConcludesStudent() {
    FormerStudent updated =
        service.addCompletedHours(formerStudent.getAccountId(), new BigDecimal("100"));

    assertThat(updated.getCounterpartHours().getConcluded()).isTrue();
    verify(enrollmentsService).completeAllByFormerStudentId(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception when adding invalid completed hours")
  void addCompletedHoursInvalid() {
    assertThrows(
        AppValidationException.class,
        () -> service.addCompletedHours(formerStudent.getAccountId(), new BigDecimal("9999")));
  }

  @Test
  @Transactional
  @DisplayName("Should delete former student")
  void deleteSuccess() {
    boolean deleted = service.delete(formerStudent.getAccountId());

    assertThat(deleted).isTrue();
    verify(accountService).delete(formerStudent.getAccountId());
    verify(auditPublisher).fireDelete(FormerStudent.class.getName(), formerStudent.getAccountId());
  }

  @Test
  @DisplayName("Should return false when deleting null")
  void deleteNull() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting former student with enrollments")
  void deleteWithEnrollments() {
    when(enrollmentsService.existsAnyByFormerStudentId(formerStudent.getAccountId()))
        .thenReturn(true);

    assertThrows(
        br.org.catolicasc.pug.shared.exceptions.BusinessRuleException.class,
        () -> service.delete(formerStudent.getAccountId()));
  }

  @Test
  @DisplayName("Should check existence by course ID")
  void existsAnyByCourseId() {
    assertThat(service.existsAnyByCourseId(course.getId())).isTrue();
  }

  private FormerStudentCreateCommand createCommand(String registration) {
    return new FormerStudentCreateCommand(
        accountCreateCommand(),
        registration,
        Campi.JOINVILLE,
        course.getId(),
        new BigDecimal("100"),
        LocalDate.now(),
        LocalDate.now().plusMonths(6));
  }

  private AccountCreateCommand accountCreateCommand() {
    return new AccountCreateCommand(
        "student-" + UuidCreator.getTimeOrderedEpoch() + "@example.com",
        AccountType.FORMER_STUDENT,
        null,
        new UserCreateCommand("12345678901", "Former Student"));
  }
}
