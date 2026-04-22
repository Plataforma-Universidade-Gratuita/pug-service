package br.org.catolicasc.pug.partner.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.vos.Cnpj;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EntityMapper Tests")
class EntityMapperTest extends CopyableMapperTest<Entity, EntityEntity> {

  @Override
  protected Entity createDomain() {
    return Entity.factory(
        Cnpj.factory(TestBrazilianIdentifierGenerator.generateValidCnpj()),
        "WEG S.A.",
        UUID.randomUUID(),
        "Address");
  }

  @Override
  protected EntityEntity createEntity() {
    return new EntityEntity();
  }

  @Override
  protected Entity mapToDomain(EntityEntity entity) {
    return EntityMapper.toDomain(entity);
  }

  @Override
  protected EntityEntity mapToEntity(Entity domain) {
    return EntityMapper.toEntity(domain);
  }

  @Override
  protected void copy(Entity domain, EntityEntity entity) {
    EntityMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Entity original, Entity mapped) {
    assertThat(mapped).isEqualTo(original);
    assertThat(mapped.getCnpj().getValue()).isEqualTo(original.getCnpj().getValue());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("Should update existing JPA Entity correctly")
  void shouldCopyProperties() {
    Entity entity = createDomain();
    EntityEntity dbEntity = createEntity();

    Entity updatedDomain = entity.rename("New WEG Name");
    EntityMapper.copy(updatedDomain, dbEntity);

    assertThat(dbEntity.getName()).isEqualTo("New WEG Name");
    assertThat(dbEntity.getCnpj()).isEqualTo(entity.getCnpj().getValue());
  }
}
