package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntityMapper Tests")
class EntityMapperTest {

  @Test
  @DisplayName("Should perform perfect round-trip (Domain -> Entity -> Domain)")
  void shouldPerformRoundTrip() {
    Entity entity =
        Entity.factory(
            Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj()),
            "WEG S.A.",
            UUID.randomUUID(),
            "Address");

    EntityEntity persistence = EntityMapper.toEntity(entity);
    Entity mapped = EntityMapper.toDomain(persistence);

    assertThat(mapped).isEqualTo(entity);
    assertThat(mapped.getCnpj().getValue()).isEqualTo(entity.getCnpj().getValue());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(entity.getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("Should return null when mapping null input")
  void shouldReturnNullOnNullInput() {
    assertThat(EntityMapper.toDomain(null)).isNull();
    assertThat(EntityMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("Should update existing JPA Entity correctly")
  void shouldCopyProperties() {
    Entity entity =
        Entity.factory(
            Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj()),
            "WEG S.A.",
            UUID.randomUUID(),
            "Old Addr");
    EntityEntity dbEntity = new EntityEntity();

    Entity updatedDomain = entity.rename("New WEG Name");
    EntityMapper.copy(updatedDomain, dbEntity);

    assertThat(dbEntity.getName()).isEqualTo("New WEG Name");
    assertThat(dbEntity.getCnpj()).isEqualTo(entity.getCnpj().getValue());
  }
}
