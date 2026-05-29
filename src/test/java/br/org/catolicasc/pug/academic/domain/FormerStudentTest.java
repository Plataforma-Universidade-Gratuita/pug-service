package br.org.catolicasc.pug.academic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FormerStudent Aggregate Tests")
class StudentTest {

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
  }
}

