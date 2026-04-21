package br.org.catolicasc.pug.partner.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Cnpj Value Object Tests")
class CnpjTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid CNPJ from raw numeric string")
    void shouldCreateValidCnpj() {
      Cnpj cnpj = Cnpj.factory("84429695000111");

      assertThat(cnpj.hasFieldErrors()).isFalse();
      assertThat(cnpj.getValue()).isEqualTo("84429695000111");
    }

    @Test
    @DisplayName("Should sanitize input (remove punctuation)")
    void shouldSanitizeInput() {
      Cnpj cnpj = Cnpj.factory("84.429.695/0001-11");

      assertThat(cnpj.hasFieldErrors()).isFalse();
      assertThat(cnpj.getValue()).isEqualTo("84429695000111");
    }

    @Test
    @DisplayName("Should reject blank/null inputs")
    void shouldRejectEmpty() {
      Cnpj cnpj = Cnpj.factory("   ");
      assertThat(cnpj.hasFieldErrors()).isTrue();
      assertThat(cnpj.getFieldErrors()).contains(PartnerFieldErrorCodes.INVALID_CNPJ_BLANK);
    }

    @Test
    @DisplayName("Should reject invalid CNPJ format/checksum")
    void shouldRejectInvalidFormat() {
      Cnpj cnpj = Cnpj.factory("12345678901234");
      assertThat(cnpj.hasFieldErrors()).isTrue();
      assertThat(cnpj.getFieldErrors()).contains(PartnerFieldErrorCodes.INVALID_CNPJ_FORMAT);
    }
  }
}
