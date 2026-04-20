package br.org.catolicasc.pug.identity.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.identity.domain.enums.IdentityFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Cpf Value Object Tests")
class CpfTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid CPF from raw numeric string")
    void shouldCreateValidCpf() {
      Cpf cpf = Cpf.factory("11144477735");

      assertThat(cpf.hasFieldErrors()).isFalse();
      assertThat(cpf.getValue()).isEqualTo("11144477735");
    }

    @Test
    @DisplayName("Should sanitize input (remove formatting)")
    void shouldSanitizeInput() {
      Cpf cpf = Cpf.factory("111.444.777-35");

      assertThat(cpf.hasFieldErrors()).isFalse();
      assertThat(cpf.getValue()).isEqualTo("11144477735");
    }

    @Test
    @DisplayName("Should reject blank/null inputs")
    void shouldRejectEmpty() {
      Cpf cpf = Cpf.factory("   ");
      assertThat(cpf.hasFieldErrors()).isTrue();
      assertThat(cpf.getFieldErrors()).contains(IdentityFieldErrorCodes.INVALID_CPF_BLANK);
    }

    @Test
    @DisplayName("Should reject invalid checksum or repeated digits")
    void shouldRejectInvalidFormat() {
      assertThat(Cpf.factory("11111111111").hasFieldErrors()).isTrue();
      assertThat(Cpf.factory("12345678901").hasFieldErrors()).isTrue();
    }
  }
}
