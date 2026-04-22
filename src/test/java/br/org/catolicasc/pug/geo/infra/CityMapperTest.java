package br.org.catolicasc.pug.geo.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.domain.vos.IbgeCode;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CityMapper Tests")
class CityMapperTest {

  @Test
  @DisplayName("Should perform a perfect round-trip (Domain -> Entity -> Domain)")
  void shouldPerformRoundTrip() {
    City city = City.factory("Jaraguá do Sul", IbgeCode.factory("4209106"));

    CityEntity entity = CityMapper.toEntity(city);
    City mappedCity = CityMapper.toDomain(entity);

    assertThat(mappedCity.getId()).isEqualTo(city.getId());
    assertThat(mappedCity.getName()).isEqualTo(city.getName());
    assertThat(mappedCity.getIbgeCode().getCode()).isEqualTo(city.getIbgeCode().getCode());
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
