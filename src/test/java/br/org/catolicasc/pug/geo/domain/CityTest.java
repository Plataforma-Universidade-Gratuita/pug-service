package br.org.catolicasc.pug.geo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.enums.GeoFieldErrorCodes;
import br.org.catolicasc.pug.geo.domain.vos.IbgeCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("City Aggregate Tests")
class CityTest {

  @Test
  @DisplayName("Should create valid City")
  void shouldCreateCity() {
    City city = City.factory("Joinville", IbgeCode.factory("4209106"));

    assertThat(city.hasFieldErrors()).isFalse();
    assertThat(city.getName()).isEqualTo("Joinville");
    assertThat(city.getIbgeCode().getCode()).isEqualTo("4209106");
  }

  @Test
  @DisplayName("Should collect errors when data is invalid")
  void shouldCollectValidationErrors() {
    City city = City.factory("  ", null);

    assertThat(city.hasFieldErrors()).isTrue();
    assertThat(city.getFieldErrors())
        .contains(
            GeoFieldErrorCodes.INVALID_IBGE_CODE_BLANK,
            br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes.INVALID_NAME_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename city successfully")
    void shouldRename() {
      City city = City.factory("Old Name", IbgeCode.factory("4209106"));
      City renamed = city.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getId()).isEqualTo(city.getId());
    }

    @Test
    @DisplayName("Should update IBGE code successfully")
    void shouldUpdateIbge() {
      City city = City.factory("Joinville", IbgeCode.factory("4209106"));
      City updated = city.changeIbgeCode(IbgeCode.factory("4209107"));

      assertThat(updated.getIbgeCode().getCode()).isEqualTo("4209107");
    }
  }
}
