package br.org.catolicasc.pug.academic.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CounterpartHours Value Object Tests")
class CounterpartHoursTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid CounterpartHours")
    void shouldCreateValidHours() {
      CounterpartHours hours =
          CounterpartHours.factory(new BigDecimal("100.00"), BigDecimal.ZERO, false);

      assertThat(hours.hasFieldErrors()).isFalse();
      assertThat(hours.getRequiredHours()).isEqualTo(new BigDecimal("100.00"));
      assertThat(hours.getConcluded()).isFalse();
    }

    @Test
    @DisplayName("Should reject non-positive required hours")
    void shouldRejectInvalidRequired() {
      CounterpartHours hours = CounterpartHours.factory(BigDecimal.ZERO, BigDecimal.ZERO, false);

      assertThat(hours.hasFieldErrors()).isTrue();
      assertThat(hours.getFieldErrors()).contains(AcademicFieldErrorCodes.INVALID_HOURS_BLANK);
    }

    @Test
    @DisplayName("Should reject negative completed hours")
    void shouldRejectNegativeCompleted() {
      CounterpartHours hours =
          CounterpartHours.factory(new BigDecimal("100"), new BigDecimal("-1"), false);

      assertThat(hours.hasFieldErrors()).isTrue();
      assertThat(hours.getFieldErrors())
          .contains(AcademicFieldErrorCodes.INVALID_COMPLETED_HOURS_NEGATIVE);
    }

    @Test
    @DisplayName("Should reject completed hours exceeding required hours")
    void shouldRejectExceedingHours() {
      CounterpartHours hours =
          CounterpartHours.factory(new BigDecimal("100"), new BigDecimal("150"), false);

      assertThat(hours.hasFieldErrors()).isTrue();
      assertThat(hours.getFieldErrors())
          .contains(AcademicFieldErrorCodes.INVALID_COMPLETED_HOURS_EXCEEDS);
    }
  }
}
