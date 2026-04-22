package br.org.catolicasc.pug.partner.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.domain.enums.PartnerFieldErrorCodes;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Entity Aggregate Tests")
class EntityTest {

  private final Cnpj validCnpj = Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj());
  private final UUID validCityId = UUID.randomUUID();

  @Test
  @DisplayName("Should create valid Entity")
  void shouldCreateEntity() {
    Entity entity =
        Entity.factory(validCnpj, "WEG S.A.", validCityId, "Av. Pref. Waldemar Grubba, 3000");

    assertThat(entity.hasFieldErrors()).isFalse();
    assertThat(entity.getName()).isEqualTo("WEG S.A.");
  }

  @Test
  @DisplayName("Should collect errors when data is invalid")
  void shouldCollectValidationErrors() {
    Entity entity = Entity.factory(null, " ", null, "");

    assertThat(entity.hasFieldErrors()).isTrue();
    assertThat(entity.getFieldErrors())
        .contains(
            PartnerFieldErrorCodes.INVALID_CNPJ_BLANK,
            SharedFieldErrorCodes.INVALID_NAME_BLANK,
            PartnerFieldErrorCodes.INVALID_CITY_ID_BLANK,
            PartnerFieldErrorCodes.INVALID_ADDRESS_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename entity successfully")
    void shouldRename() {
      Entity entity = Entity.factory(validCnpj, "Original Name", validCityId, "Address");
      Entity renamed = entity.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(entity.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should update address successfully")
    void shouldMoveAddress() {
      Entity entity = Entity.factory(validCnpj, "Name", validCityId, "Old Address");
      Entity moved = entity.moveToAddress("New Address");

      assertThat(moved.getAddress()).isEqualTo("New Address");
    }

    @Test
    @DisplayName("Should move to another city successfully")
    void shouldMoveCity() {
      UUID newCityId = UUID.randomUUID();
      Entity entity = Entity.factory(validCnpj, "Name", validCityId, "Address");
      Entity moved = entity.moveToCity(newCityId);

      assertThat(moved.getCityId()).isEqualTo(newCityId);
    }
  }
}
