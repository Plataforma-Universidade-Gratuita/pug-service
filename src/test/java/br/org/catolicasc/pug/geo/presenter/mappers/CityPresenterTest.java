package br.org.catolicasc.pug.geo.presenter.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.presenter.dtos.CityResponse;
import io.quarkus.test.junit.QuarkusTest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CityPresenter Coverage")
class CityPresenterTest {

  @Nested
  @DisplayName("Mapping Logic Tests")
  class MappingTests {

    @Test
    @DisplayName("Should return null when input view is null")
    void shouldReturnNullOnViewNull() {
      assertThat(CityPresenter.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Should map CityView to CityResponse correctly")
    void shouldMapSuccessfully() {
      UUID id = UUID.randomUUID();
      CityView view = new CityView(id, "Joinville", "4209106");

      CityResponse response = CityPresenter.toResponse(view);

      assertThat(response).isNotNull();
      assertThat(response.id()).isEqualTo(id);
      assertThat(response.name()).isEqualTo("Joinville");
      assertThat(response.ibgeCode()).isEqualTo("4209106");
    }
  }
}
