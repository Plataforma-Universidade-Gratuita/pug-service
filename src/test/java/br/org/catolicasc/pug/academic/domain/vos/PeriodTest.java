package br.org.catolicasc.pug.academic.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Period Value Object Tests")
class PeriodTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid Period")
    void shouldCreateValidPeriod() {
      LocalDate start = LocalDate.of(2026, 1, 1);
      LocalDate end = LocalDate.of(2026, 6, 30);
      Period period = Period.factory(start, end);

      assertThat(period.hasFieldErrors()).isFalse();
      assertThat(period.getStartDate()).isEqualTo(start);
      assertThat(period.getDueDate()).isEqualTo(end);
    }

    @Test
    @DisplayName("Should reject when dates are null")
    void shouldRejectNullDates() {
      Period period = Period.factory(null, null);

      assertThat(period.hasFieldErrors()).isTrue();
      assertThat(period.getFieldErrors())
          .contains(
              AcademicFieldErrorCodes.INVALID_START_DATE_BLANK,
              AcademicFieldErrorCodes.INVALID_DUE_DATE_BLANK);
    }

    @Test
    @DisplayName("Should reject when due date is before start date")
    void shouldRejectChronologicalInvalid() {
      LocalDate start = LocalDate.of(2026, 6, 30);
      LocalDate end = LocalDate.of(2026, 1, 1);
      Period period = Period.factory(start, end);

      assertThat(period.hasFieldErrors()).isTrue();
      assertThat(period.getFieldErrors()).contains(AcademicFieldErrorCodes.INVALID_PERIOD_RANGE);
    }
  }
}
