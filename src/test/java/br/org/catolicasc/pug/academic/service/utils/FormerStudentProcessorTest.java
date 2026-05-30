package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.accounts.AccountCreateCommand;
import br.org.catolicasc.pug.identity.service.dtos.users.UserCreateCommand;
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

@DisplayName("FormerStudentProcessor Coverage")
class FormerStudentProcessorTest {

  @Test
  @DisplayName("Should process create input with zero completed hours")
  void shouldProcessCreateInput() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    FormerStudent formerStudent =
        FormerStudentProcessor.processCreateInput(
            accountId,
            "REG12345",
            Campi.JOINVILLE,
            courseId,
            new BigDecimal("100"),
            LocalDate.now(),
            LocalDate.now().plusMonths(6));

    assertThat(formerStudent.hasFieldErrors()).isFalse();
    assertThat(formerStudent.getAccountId()).isEqualTo(accountId);
    assertThat(formerStudent.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should update all fields via processUpdateInput")
  void shouldUpdateAllFields() {
    FormerStudent existing = createValidStudent();
    UUID newCourseId = UuidCreator.getTimeOrderedEpoch();
    FormerStudent updated =
        FormerStudentProcessor.processUpdateInput(
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
    FormerStudent existing = createValidStudent();
    FormerStudent updated =
        FormerStudentProcessor.processUpdateInput(existing, null, null, null, null, null, null);

    assertThat(updated).isEqualTo(existing);
  }

  @Test
  @DisplayName("Should update only startDate keeping dueDate")
  void shouldUpdateOnlyStartDate() {
    FormerStudent existing = createValidStudent();
    LocalDate newStart = LocalDate.now().plusDays(10);
    FormerStudent updated =
        FormerStudentProcessor.processUpdateInput(existing, null, null, null, null, newStart, null);

    assertThat(updated.getPeriod().getStartDate()).isEqualTo(newStart);
    assertThat(updated.getPeriod().getDueDate()).isEqualTo(existing.getPeriod().getDueDate());
  }

  @Test
  @DisplayName("Should update only dueDate keeping startDate")
  void shouldUpdateOnlyDueDate() {
    FormerStudent existing = createValidStudent();
    LocalDate newDue = LocalDate.now().plusMonths(12);
    FormerStudent updated =
        FormerStudentProcessor.processUpdateInput(existing, null, null, null, null, null, newDue);

    assertThat(updated.getPeriod().getDueDate()).isEqualTo(newDue);
    assertThat(updated.getPeriod().getStartDate()).isEqualTo(existing.getPeriod().getStartDate());
  }

  @Test
  @DisplayName("Should process bulk create input successfully")
  void shouldProcessBulkCreateInput() {
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    FormerStudentCreateCommand cmd1 = createCmd("REG001", courseId);
    FormerStudentCreateCommand cmd2 = createCmd("REG002", courseId);

    UUID acc1 = UuidCreator.getTimeOrderedEpoch();
    UUID acc2 = UuidCreator.getTimeOrderedEpoch();

    List<FormerStudent> students =
        FormerStudentProcessor.processBulkCreateInput(List.of(cmd1, cmd2), List.of(acc1, acc2));

    assertThat(students).hasSize(2);
    assertThat(students.get(0).getAccountId()).isEqualTo(acc1);
    assertThat(students.get(1).getAccountId()).isEqualTo(acc2);
  }

  @Test
  @DisplayName("Should throw when bulk sizes mismatch")
  void shouldThrowOnMismatchedSizes() {
    FormerStudentCreateCommand cmd1 = createCmd("REG001", UuidCreator.getTimeOrderedEpoch());

    assertThrows(
        IllegalArgumentException.class,
        () -> FormerStudentProcessor.processBulkCreateInput(List.of(cmd1), List.of()));
  }

  @Test
  @DisplayName("Should throw AppValidationException for invalid bulk input")
  void shouldThrowOnInvalidBulkInput() {
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            "x@x.com", AccountType.FORMER_STUDENT, "hash", new UserCreateCommand("12345678901", "Name"));
    FormerStudentCreateCommand badCmd =
        new FormerStudentCreateCommand(accCmd, "", null, null, null, null, null);

    assertThrows(
        AppValidationException.class,
        () ->
            FormerStudentProcessor.processBulkCreateInput(
                List.of(badCmd), List.of(UuidCreator.getTimeOrderedEpoch())));
  }

  /* --- helpers --- */

  private FormerStudent createValidStudent() {
    return FormerStudent.factory(
        UuidCreator.getTimeOrderedEpoch(),
        AcademicRegistration.factory("REG12345"),
        Campi.JOINVILLE,
        UuidCreator.getTimeOrderedEpoch(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }

  private FormerStudentCreateCommand createCmd(String reg, UUID courseId) {
    AccountCreateCommand accCmd =
        new AccountCreateCommand(
            "x@x.com", AccountType.FORMER_STUDENT, "hash", new UserCreateCommand("12345678901", "Name"));
    return new FormerStudentCreateCommand(
        accCmd,
        reg,
        Campi.JOINVILLE,
        courseId,
        new BigDecimal("100"),
        LocalDate.now(),
        LocalDate.now().plusMonths(6));
  }
}

