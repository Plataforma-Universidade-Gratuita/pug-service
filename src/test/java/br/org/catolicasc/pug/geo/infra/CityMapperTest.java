package br.org.catolicasc.pug.geo.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.domain.vos.IbgeCode;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.helpers.AbstractMapperTest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CityMapper Tests")
class CityMapperTest extends AbstractMapperTest<City, CityEntity> {

  @Override
  protected City createDomain() {
    return City.factory("Jaraguá do Sul", IbgeCode.factory("4209106"));
  }

  @Override
  protected City mapToDomain(CityEntity entity) {
    return CityMapper.toDomain(entity);
  }

  @Override
  protected CityEntity mapToEntity(City domain) {
    return CityMapper.toEntity(domain);
  }

  @Override
  protected void assertRoundTrip(City original, City mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getName()).isEqualTo(original.getName());
    assertThat(mapped.getIbgeCode().getCode()).isEqualTo(original.getIbgeCode().getCode());
  }

  @Test
  @DisplayName("Should project CityEntity to CityView correctly")
  void shouldProjectToView() {
    CityEntity entity = new CityEntity();
    entity.setId(UUID.randomUUID());
    entity.setName("Jaraguá do Sul");
    entity.setIbgeCode("4209106");

    CityView view = CityMapper.toView(entity);

    assertThat(view.id()).isEqualTo(entity.getId());
    assertThat(view.name()).isEqualTo(entity.getName());
    assertThat(view.ibgeCode()).isEqualTo(entity.getIbgeCode());
  }

  @Test
  @DisplayName("Should return null when projecting null CityEntity to CityView")
  void shouldReturnNullOnViewNull() {
    assertThat(CityMapper.toView(null)).isNull();
  }
}
