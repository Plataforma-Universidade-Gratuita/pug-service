package br.org.catolicasc.pug.academic.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AreaOfExpertiseRepositoryImpl Tests")
class AreaOfExpertiseRepositoryImplTest {

  @Inject AreaOfExpertiseRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @Test
  @Transactional
  @DisplayName("Should persist and find AreaOfExpertise")
  void shouldPersistAndFind() {
    AreaOfExpertise areaOfExpertise =
        repository.persist(AreaOfExpertise.factory("Area " + UuidCreator.getTimeOrderedEpoch()));

    var found = repository.findOptionalById(areaOfExpertise.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(areaOfExpertise.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return null when persisting null")
  void persistShouldReturnNullForNullEntity() {
    assertThat(repository.persist((AreaOfExpertise) null)).isNull();
  }

  @Test
  @Transactional
  @DisplayName("Should check existence by name")
  void shouldCheckExistsByName() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();

    assertThat(repository.existsByName(areaOfExpertise.getName())).isTrue();
    assertThat(repository.existsByName("Missing " + UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should update managed AreaOfExpertise")
  void shouldUpdate() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    AreaOfExpertise renamed =
        areaOfExpertise.rename("Updated " + UuidCreator.getTimeOrderedEpoch());

    repository.update(renamed);

    var found = repository.findOptionalById(areaOfExpertise.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(renamed.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should ignore null update and update with null ID")
  void shouldIgnoreInvalidUpdates() {
    repository.update(null);
    repository.update(AreaOfExpertise.builder().id(null).name("No ID").auditInfo(null).build());
  }

  @Test
  @Transactional
  @DisplayName("Should ignore update when entity is not found")
  void shouldIgnoreUpdateWhenManagedEntityDoesNotExist() {
    AreaOfExpertise missing =
        AreaOfExpertise.factory("Missing " + UuidCreator.getTimeOrderedEpoch());

    repository.update(missing);

    assertThat(repository.findOptionalById(missing.getId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should delete by ID")
  void shouldDeleteById() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();

    assertThat(repository.deleteById(areaOfExpertise.getId())).isTrue();
    em.clear();
    assertThat(repository.findOptionalById(areaOfExpertise.getId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting null or missing ID")
  void shouldReturnFalseWhenDeletingInvalidId() {
    assertThat(repository.deleteById(null)).isFalse();
    assertThat(repository.deleteById(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }
}
