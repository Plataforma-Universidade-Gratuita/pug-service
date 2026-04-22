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

@DisplayName("Student Aggregate Tests")
class StudentTest {

  private final AcademicRegistration validReg = AcademicRegistration.factory("12345");
  private final CounterpartHours validHours =
      CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false);
  private final Period validPeriod = Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6));

  @Test
  @DisplayName("Should create valid Student")
  void shouldCreateStudent() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();

    Student student =
        Student.factory(
            accountId, validReg, Campi.JARAGUA_DO_SUL, courseId, validHours, validPeriod);

    assertThat(student.hasFieldErrors()).isFalse();
    assertThat(student.getAccountId()).isEqualTo(accountId);
  }

  @Test
  @DisplayName("Should collect errors when all fields are invalid")
  void shouldCollectValidationErrors() {
    Student student = Student.factory(null, null, null, null, null, null);

    assertThat(student.hasFieldErrors()).isTrue();
    assertThat(student.getFieldErrors())
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
      Student student =
          Student.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      Thread.sleep(10);
      Student updated = student.addCompletedHours(new BigDecimal("50"));

      assertThat(updated.getCounterpartHours().getCompletedHours()).isEqualTo(new BigDecimal("50"));
      assertThat(updated.getAuditInfo().getUpdatedAt())
          .isAfter(student.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should update course successfully")
    void shouldChangeCourse() {
      Student student =
          Student.factory(
              UuidCreator.getTimeOrderedEpoch(),
              validReg,
              Campi.JARAGUA_DO_SUL,
              UuidCreator.getTimeOrderedEpoch(),
              validHours,
              validPeriod);
      UUID newCourse = UuidCreator.getTimeOrderedEpoch();

      Student updated = student.changeCourse(newCourse);

      assertThat(updated.getCourseId()).isEqualTo(newCourse);
    }
  }
}
