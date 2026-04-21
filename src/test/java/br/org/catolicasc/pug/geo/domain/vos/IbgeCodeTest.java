package br.org.catolicasc.pug.geo.domain.vos;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.enums.GeoFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("IbgeCode Value Object Tests")
class IbgeCodeTest {

  @Nested
  @DisplayName("Factory and Validation")
  class FactoryTests {

    @Test
    @DisplayName("Should create valid IbgeCode from 7-digit numeric string")
    void shouldCreateValidIbgeCode() {
      IbgeCode ibge = IbgeCode.factory("4209106");

      assertThat(ibge.hasFieldErrors()).isFalse();
      assertThat(ibge.getCode()).isEqualTo("4209106");
    }

    @Test
    @DisplayName("Should reject blank/null inputs")
    void shouldRejectEmpty() {
      IbgeCode ibge = IbgeCode.factory("   ");
      assertThat(ibge.hasFieldErrors()).isTrue();
      assertThat(ibge.getFieldErrors()).contains(GeoFieldErrorCodes.INVALID_IBGE_CODE_BLANK);
    }

    @Test
    @DisplayName("Should reject non-numeric characters")
    void shouldRejectNonNumeric() {
      IbgeCode ibge = IbgeCode.factory("420910A");
      assertThat(ibge.hasFieldErrors()).isTrue();
      assertThat(ibge.getFieldErrors()).contains(GeoFieldErrorCodes.INVALID_IBGE_CODE_FORMAT);
    }

    @Test
    @DisplayName("Should reject code with length other than 7")
    void shouldRejectInvalidLength() {
      IbgeCode ibge = IbgeCode.factory("420910");
      assertThat(ibge.hasFieldErrors()).isTrue();
      assertThat(ibge.getFieldErrors()).contains(GeoFieldErrorCodes.INVALID_IBGE_CODE_FORMAT);
    }
  }
}
