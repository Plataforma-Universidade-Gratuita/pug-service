package br.org.catolicasc.pug.geo.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.City;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CityRepositoryImplTest {

  @Inject CityRepositoryImpl repository;

  @Test
  @DisplayName("Should find pre-seeded City by ID")
  void shouldFindExistingCity() {
    var allCities = repository.findAll().list();
    var joinville =
        allCities.stream().filter(c -> c.getName().equals("Joinville")).findFirst().orElseThrow();

    Optional<City> found = repository.findOptionalById(joinville.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Joinville");
    assertThat(found.get().getIbgeCode().getCode()).isEqualTo("4209106");
  }
}
