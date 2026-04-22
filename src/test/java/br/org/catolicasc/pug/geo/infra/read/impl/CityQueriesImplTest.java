package br.org.catolicasc.pug.geo.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@QuarkusTest
@DisplayName("CityQueriesImpl Coverage")
class CityQueriesImplTest extends BaseSearchTest {

  @Inject CityQueriesImpl queries;
  @Inject EntityManager em;

  @Test
  @DisplayName("Should return empty when ID is null")
  void findByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @DisplayName("Should find city by ID successfully")
  void findByIdSuccess() {
    CityEntity entity =
        em.createQuery("from CityEntity", CityEntity.class).setMaxResults(1).getSingleResult();
    Optional<CityView> found = queries.findOptionalById(entity.getId());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(entity.getId());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Should return empty when IBGE code is null or empty")
  void findByIbgeInvalid(String code) {
    assertThat(queries.findOptionalByIbgeCode(code)).isEmpty();
  }

  @Test
  @DisplayName("Should find city by IBGE code successfully")
  void findByIbgeSuccess() {
    Optional<CityView> found = queries.findOptionalByIbgeCode("4209106"); // Joinville
    assertThat(found).isPresent();
    assertThat(found.get().ibgeCode()).isEqualTo("4209106");
  }

  @Test
  @DisplayName("Should return empty list when searching invalid/non-existent IDs")
  void listAllByIdsInvalid() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of(UUID.randomUUID()))).isEmpty();
  }

  @Test
  @DisplayName("Should list cities by IDs successfully")
  void listAllByIdsSuccess() {
    CityEntity e =
        em.createQuery("from CityEntity", CityEntity.class).setMaxResults(1).getSingleResult();
    List<CityView> found = queries.listAllByIds(List.of(e.getId()));
    assertThat(found).hasSize(1);
  }

  @Test
  @DisplayName("Should list all cities")
  void listAllCities() {
    List<CityView> cities = queries.listAllCities();
    assertThat(cities).hasSizeGreaterThan(200); // We have 295 seeded
  }

  @Test
  @DisplayName("Should return empty list for non-matching search")
  void searchNotFound() {
    assertThat(queries.searchByName("NonExistentCity12345")).isEmpty();
  }

  @Test
  @DisplayName("Should search cities by name successfully")
  void searchByNameSuccess() throws Exception {
    List<CityView> found = queries.searchByName("Joinville");
    assertThat(found).anyMatch(c -> c.name().equals("Joinville"));
  }

  @Test
  @DisplayName("Should return empty for null/empty search query")
  void searchByNameInvalid() {
    assertThat(queries.searchByName(null)).isEmpty();
    assertThat(queries.searchByName("   ")).isEmpty();
  }
}
