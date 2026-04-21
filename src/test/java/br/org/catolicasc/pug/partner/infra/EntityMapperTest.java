package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntityMapper Tests")
class EntityMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Entity")
  void shouldPerformRoundTrip() {
    Entity entity =
        Entity.factory(Cnpj.factory("84429695000111"), "WEG S.A.", UUID.randomUUID(), "Address");

    EntityEntity persistence = EntityMapper.toEntity(entity);
    Entity mapped = EntityMapper.toDomain(persistence);

    assertThat(mapped.getId()).isEqualTo(entity.getId());
    assertThat(mapped.getCnpj().getValue()).isEqualTo(entity.getCnpj().getValue());
    assertThat(mapped.getName()).isEqualTo(entity.getName());
  }
}
