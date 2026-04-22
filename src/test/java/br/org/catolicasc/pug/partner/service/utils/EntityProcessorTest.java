package br.org.catolicasc.pug.partner.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntityProcessor Coverage")
class EntityProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    UUID cityId = UuidCreator.getTimeOrderedEpoch();
    String cnpj = TestBrazilianIdentifierGenerator.generateValidCnpj();
    Entity entity = EntityProcessor.processCreateInput(cnpj, "WEG S.A.", cityId, "Address");

    assertThat(entity.hasFieldErrors()).isFalse();
    assertThat(entity.getName()).isEqualTo("WEG S.A.");
    assertThat(entity.getCnpj().getValue()).isEqualTo(cnpj);
  }

  @Test
  @DisplayName("Should collect validation errors for invalid input")
  void shouldCollectErrors() {
    Entity entity = EntityProcessor.processCreateInput("", "", null, "");

    assertThat(entity.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should mutate fields correctly via update")
  void shouldUpdateEntity() {
    Entity existing =
        Entity.factory(
            Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj()),
            "Old Name",
            UuidCreator.getTimeOrderedEpoch(),
            "Old Addr");
    UUID newCity = UuidCreator.getTimeOrderedEpoch();

    Entity updated = EntityProcessor.processUpdateInput(existing, "New Name", newCity, "New Addr");

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getCityId()).isEqualTo(newCity);
    assertThat(updated.getAddress()).isEqualTo("New Addr");
  }

  @Test
  @DisplayName("Should skip update if input values are null/empty")
  void shouldSkipNullUpdates() {
    Entity existing =
        Entity.factory(
            Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj()),
            "Name",
            UuidCreator.getTimeOrderedEpoch(),
            "Addr");

    Entity updated = EntityProcessor.processUpdateInput(existing, null, null, "");

    assertThat(updated).isEqualTo(existing);
  }
}
