package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.academic.domain.Student;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.service.dtos.StudentCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.UserCreateCommand;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentProcessor Coverage")
class StudentProcessorTest {

  @Test
  @DisplayName("Should process create input with zero completed hours")
  void shouldProcessCreateInput() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    Student student =
        StudentProcessor.processCreateInput(
            accountId,
            "REG12345",
            Campi.JOINVILLE,
            courseId,
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    assertThat(student.hasFieldErrors()).isFalse();
    assertThat(student.getAccountId()).isEqualTo(accountId);
    assertThat(student.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should update all fields via processUpdateInput")
  void shouldUpdateAllFields() {
    Student existing = createValidStudent();
    UUID newCourseId = UuidCreator.getTimeOrderedEpoch();
    Student updated =
        StudentProcessor.processUpdateInput(
            existing,
            "NEWREG999",
            Campi.JOINVILLE,
            newCourseId,
            new BigDecimal("200"),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusMonths(12));

    assertThat(updated.getAcademicRegistration().getValue()).isEqualTo("NEWREG999");
    assertThat(updated.getCampus()).isEqualTo(Campi.JOINVILLE);
    assertThat(updated.getCourseId()).isEqualTo(newCourseId);
    assertThat(updated.getCounterpartHours().getRequiredHours())
        .isEqualByComparingTo(new BigDecimal("200"));
  }

  @Test
  @DisplayName("Should skip update if all inputs are null/empty")
  void shouldSkipNullUpdates() {
    Student existing = createValidStudent();
    Student updated =
        StudentProcessor.processUpdateInput(existing, null, null, null, null, null, null);

    assertThat(updated).isEqualTo(existing);
  }

  @Test
  @DisplayName("Should update only startDate keeping dueDate")
  void shouldUpdateOnlyStartDate() {
    Student existing = createValidStudent();
    LocalDate newStart = LocalDate.now().plusDays(10);
    Student updated =
        StudentProcessor.processUpdateInput(existing, null, null, null, null, newStart, null);

    assertThat(updated.getPeriod().getStartDate()).isEqualTo(newStart);
    assertThat(updated.getPeriod().getDueDate()).isEqualTo(existing.getPeriod().getDueDate());
  }

  @Test
  @DisplayName("Should update only dueDate keeping startDate")
  void shouldUpdateOnlyDueDate() {
    Student existing = createValidStudent();
    LocalDate newDue = LocalDate.now().plusMonths(12);
    Student updated =
        StudentProcessor.processUpdateInput(existing, null, null, null, null, null, newDue);

    assertThat(updated.getPeriod().getDueDate()).isEqualTo(newDue);
    assertThat(updated.getPeriod().getStartDate()).isEqualTo(existing.getPeriod().getStartDate());
  }

  @Test
  @DisplayName("Should process bulk create input successfully")
  void shouldProcessBulkCreateInput() {
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    StudentCreateCommand cmd1 = createCmd("REG001", courseId);
    StudentCreateCommand cmd2 = createCmd("REG002", courseId);

    UUID acc1 = UuidCreator.getTimeOrderedEpoch();
    UUID acc2 = UuidCreator.getTimeOrderedEpoch();

    List<Student> students =
        StudentProcessor.processBulkCreateInput(List.of(cmd1, cmd2), List.of(acc1, acc2));

    assertThat(students).hasSize(2);
    assertThat(students.get(0).getAccountId()).isEqualTo(acc1);
    assertThat(students.get(1).getAccountId()).isEqualTo(acc2);
  }

  @Test
  @DisplayName("Should throw when bulk sizes mismatch")
  void shouldThrowOnMismatchedSizes() {
    StudentCreateCommand cmd1 = createCmd("REG001", UuidCreator.getTimeOrderedEpoch());

    assertThrows(
        IllegalArgumentException.class,
        () -> StudentProcessor.processBulkCreateInput(List.of(cmd1), List.of()));
  }

  @Test
  @DisplayName("Should throw AppValidationException for invalid bulk input")
  void shouldThrowOnInvalidBulkInput() {
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            "x@x.com", AccountType.STUDENT, "hash", new UserCreateCommand("12345678901", "Name"));
    StudentCreateCommand badCmd =
        new StudentCreateCommand(accCmd, "", null, null, null, null, null);

    assertThrows(
        AppValidationException.class,
        () ->
            StudentProcessor.processBulkCreateInput(
                List.of(badCmd), List.of(UuidCreator.getTimeOrderedEpoch())));
  }

  /* --- helpers --- */

  private Student createValidStudent() {
    return Student.factory(
        UuidCreator.getTimeOrderedEpoch(),
        AcademicRegistration.factory("REG12345"),
        Campi.JOINVILLE,
        UuidCreator.getTimeOrderedEpoch(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }

  private StudentCreateCommand createCmd(String reg, UUID courseId) {
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            "x@x.com", AccountType.STUDENT, "hash", new UserCreateCommand("12345678901", "Name"));
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
