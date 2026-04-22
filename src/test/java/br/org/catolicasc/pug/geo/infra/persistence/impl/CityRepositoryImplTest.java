package br.org.catolicasc.pug.geo.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CityRepositoryImpl Coverage")
class CityRepositoryImplTest {

  @Inject CityRepositoryImpl repository;
  @Inject EntityManager em;

  @Test
  @DisplayName("Should return empty when ID is null")
  void findOptionalByIdNull() {
    assertThat(repository.findOptionalById(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty when ID does not exist")
  void findOptionalByIdNotFound() {
    assertThat(repository.findOptionalById(UUID.randomUUID())).isEmpty();
  }

  @Test
  @DisplayName("Should successfully find and map City to Domain")
  void findOptionalByIdSuccess() {
    CityEntity entity =
        em.createQuery("from CityEntity", CityEntity.class).setMaxResults(1).getSingleResult();

    Optional<City> result = repository.findOptionalById(entity.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(entity.getId());
    assertThat(result.get().getName()).isEqualTo(entity.getName());
    assertThat(result.get().getIbgeCode().getCode()).isEqualTo(entity.getIbgeCode());
  }
}
