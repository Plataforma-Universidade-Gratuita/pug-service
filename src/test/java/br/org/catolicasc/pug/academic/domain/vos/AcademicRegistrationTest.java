package br.org.catolicasc.pug.academic.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.enums.AcademicFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AcademicRegistration Value Object Tests")
class AcademicRegistrationTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid AcademicRegistration from string")
    void shouldCreateValidRegistration() {
      AcademicRegistration reg = AcademicRegistration.factory("123456789012345");

      assertThat(reg.hasFieldErrors()).isFalse();
      assertThat(reg.getValue()).isEqualTo("123456789012345");
    }

    @Test
    @DisplayName("Should trim whitespace")
    void shouldTrimWhitespace() {
      AcademicRegistration reg = AcademicRegistration.factory("  REG123  ");
      assertThat(reg.hasFieldErrors()).isFalse();
      assertThat(reg.getValue()).isEqualTo("REG123");
    }

    @Test
    @DisplayName("Should reject blank/null inputs")
    void shouldRejectEmpty() {
      AcademicRegistration reg = AcademicRegistration.factory("   ");
      assertThat(reg.hasFieldErrors()).isTrue();
      assertThat(reg.getFieldErrors()).contains(AcademicFieldErrorCodes.INVALID_REGISTRATION_BLANK);
    }

    @Test
    @DisplayName("Should reject registration exceeding 15 characters")
    void shouldRejectTooLong() {
      AcademicRegistration reg = AcademicRegistration.factory("1234567890123456");
      assertThat(reg.hasFieldErrors()).isTrue();
      assertThat(reg.getFieldErrors())
          .contains(AcademicFieldErrorCodes.INVALID_REGISTRATION_TOO_LONG);
    }
  }
}
