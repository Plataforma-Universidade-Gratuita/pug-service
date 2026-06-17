package br.org.catolicasc.pug.academic.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FormerStudent Aggregate Tests")
class FormerStudentTest {

  private final AcademicRegistration validReg = AcademicRegistration.factory("12345");
  private final CounterpartHours validHours =
      CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false);
  private final Period validPeriod = Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6));

  @Test
  @DisplayName("Should create valid FormerStudent")
  void shouldCreateStudent() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();

    FormerStudent formerStudent =
        FormerStudent.factory(
            accountId, validReg, Campi.JARAGUA_DO_SUL, courseId, validHours, validPeriod);

    assertThat(formerStudent.hasFieldErrors()).isFalse();
    assertThat(formerStudent.getAccountId()).isEqualTo(accountId);
  }

  @Test
  @DisplayName("Should collect errors when all fields are invalid")
  void shouldCollectValidationErrors() {
    FormerStudent formerStudent = FormerStudent.factory(null, null, null, null, null, null);

    assertThat(formerStudent.hasFieldErrors()).isTrue();
    assertThat(formerStudent.getFieldErrors())
        .contains(
            AcademicFieldErrorCodes.INVALID_ACCOUNT_ID_BLANK,
            AcademicFieldErrorCodes.INVALID_REGISTRATION_BLANK,
            SharedFieldErrorCodes.INVALID_CAMPUS_BLANK,
            AcademicFieldErrorCodes.INVALID_COURSE_BLANK,
            AcademicFieldErrorCodes.INVALID_HOURS_BLANK,
            AcademicFieldErrorCodes.INVALID_PERIOD_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should add completed hours and update status")
    void shouldAddHours() throws InterruptedException {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      Thread.sleep(10);
      FormerStudent updated = formerStudent.addCompletedHours(new BigDecimal("50"));

      assertThat(updated.getCounterpartHours().getCompletedHours()).isEqualTo(new BigDecimal("50"));
      assertThat(updated.getAuditInfo().getUpdatedAt())
          .isAfter(formerStudent.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should update course successfully")
    void shouldChangeCourse() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      UUID newCourse = UuidCreator.getTimeOrderedEpoch();

      FormerStudent updated = formerStudent.changeCourse(newCourse);

      assertThat(updated.getCourseId()).isEqualTo(newCourse);
    }

    @Test
    @DisplayName("Should keep same instance when campus is unchanged")
    void shouldKeepSameInstanceWhenCampusIsUnchanged() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);

      FormerStudent updated = formerStudent.moveToCampus(Campi.JARAGUA_DO_SUL);

      assertThat(updated).isSameAs(formerStudent);
    }

    @Test
    @DisplayName("Should update academic registration successfully")
    void shouldChangeAcademicRegistration() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      AcademicRegistration newRegistration = AcademicRegistration.factory("54321");

      FormerStudent updated = formerStudent.changeAcademicRegistration(newRegistration);

      assertThat(updated.getAcademicRegistration()).isEqualTo(newRegistration);
    }

    @Test
    @DisplayName("Should update required hours successfully")
    void shouldUpdateRequiredHours() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      CounterpartHours newHours =
          CounterpartHours.factory(new BigDecimal("120"), new BigDecimal("20"), false);

      FormerStudent updated = formerStudent.updateRequiredHours(newHours);

      assertThat(updated.getCounterpartHours()).isEqualTo(newHours);
    }

    @Test
    @DisplayName("Should update period successfully")
    void shouldUpdateDateWindow() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      Period newPeriod = Period.factory(LocalDate.now().plusDays(1), LocalDate.now().plusMonths(8));

      FormerStudent updated = formerStudent.updateDateWindow(newPeriod);

      assertThat(updated.getPeriod()).isEqualTo(newPeriod);
    }

    @Test
    @DisplayName("Should remove completed hours and recalculate concluded status")
    void shouldRemoveCompletedHours() {
      CounterpartHours concludedHours =
          CounterpartHours.factory(new BigDecimal("100"), new BigDecimal("100"), true);
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              concludedHours,
              validPeriod);

      FormerStudent updated = formerStudent.removeCompletedHours(new BigDecimal("10"));

      assertThat(updated.getCounterpartHours().getCompletedHours()).isEqualTo(new BigDecimal("90"));
      assertThat(updated.getCounterpartHours().getConcluded()).isFalse();
    }

    @Test
    @DisplayName("Should allow adding completed hours within required limit")
    void shouldAllowAddingCompletedHoursWithinLimit() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);

      assertDoesNotThrow(() -> formerStudent.validateCanAddCompletedHours(new BigDecimal("50")));
    }

    @Test
    @DisplayName("Should fail when adding completed hours exceeds required limit")
    void shouldFailWhenAddingCompletedHoursExceedsLimit() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);

      assertThrows(
          BusinessRuleException.class,
          () -> formerStudent.validateCanAddCompletedHours(new BigDecimal("101")));
    }

    @Test
    @DisplayName("Should allow removing completed hours without going negative")
    void shouldAllowRemovingCompletedHoursWithoutGoingNegative() {
      FormerStudent formerStudent =
          FormerStudent.factory(
                  UuidCreator.getTimeOrderedEpoch(),
                  validReg,
                  Campi.JARAGUA_DO_SUL,
                  UuidCreator.getTimeOrderedEpoch(),
                  validHours,
                  validPeriod)
              .addCompletedHours(new BigDecimal("50"));

      assertDoesNotThrow(() -> formerStudent.validateCanRemoveCompletedHours(new BigDecimal("50")));
    }

    @Test
    @DisplayName("Should fail when removing completed hours goes negative")
    void shouldFailWhenRemovingCompletedHoursGoesNegative() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);

      assertThrows(
          BusinessRuleException.class,
          () -> formerStudent.validateCanRemoveCompletedHours(BigDecimal.ONE));
    }

    @Test
    @DisplayName("Should allow enrollment when counterpart hours are not concluded")
    void shouldAllowEnrollmentWhenNotConcluded() {
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);

      assertDoesNotThrow(formerStudent::validateCanEnroll);
    }

    @Test
    @DisplayName("Should fail enrollment when counterpart hours are concluded")
    void shouldFailEnrollmentWhenConcluded() {
      CounterpartHours concludedHours =
          CounterpartHours.factory(new BigDecimal("100"), new BigDecimal("100"), true);
      FormerStudent formerStudent =
          FormerStudent.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              concludedHours,
              validPeriod);

      assertThrows(BusinessRuleException.class, formerStudent::validateCanEnroll);
    }
  }
}
