package br.org.catolicasc.pug.geo.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CityQueriesImpl Tests")
class CityQueriesImplTest {

  @Inject CityQueriesImpl queries;

  @Test
  @DisplayName("Should retrieve city view by IBGE code")
  void shouldFindByIbgeCode() {
    Optional<CityView> view = queries.findOptionalByIbgeCode("4209106");

    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo("Joinville");
    assertThat(view.get().ibgeCode()).isEqualTo("4209106");
  }

  @Test
  @DisplayName("Should list all cities sorted by name")
  void shouldListAll() {
    List<CityView> cities = queries.listAllCities();

    assertThat(cities).isNotEmpty();
    assertThat(cities.get(0).name()).isEqualTo("Abdon Batista");
  }

  @Test
  @DisplayName("Should search city by name using full-text")
  void shouldSearchByName() {
    List<CityView> results = queries.searchByName("Joinville");

    assertThat(results).anyMatch(c -> c.name().equals("Joinville"));
  }
}
