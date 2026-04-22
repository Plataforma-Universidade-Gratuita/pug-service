package br.org.catolicasc.pug.partner.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
@DisplayName("EntityRepositoryImpl Coverage")
class EntityRepositoryImplTest {

  @Inject EntityRepositoryImpl repository;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @Test
  @DisplayName("Should persist and find Entity successfully")
  void shouldPersistAndFind() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();
    em.clear();

    Optional<Entity> found = repository.findOptionalById(entity.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(entity.getName());
  }

  @Test
  @DisplayName("Should detect existence by CityID and CNPJ")
  void existsChecks() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    assertThat(repository.existsByCityId(entity.getCityId())).isTrue();
    assertThat(repository.existsByCnpj(entity.getCnpj().getValue())).isTrue();

    assertThat(repository.existsByCityId(UuidCreator.getTimeOrderedEpoch())).isFalse();
    assertThat(repository.existsByCnpj("00000000000000")).isFalse();
  }

  @Test
  @DisplayName("Should update existing Entity")
  void shouldUpdate() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    Entity updated = entity.rename("New Name");
    repository.update(updated);
    em.flush();
    em.clear();

    assertThat(repository.findOptionalById(entity.getId()).get().getName()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("Should delete entity by ID")
  void shouldDelete() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    boolean deleted = repository.deleteById(entity.getId());
    assertThat(deleted).isTrue();
    assertThat(repository.findOptionalById(entity.getId())).isEmpty();
  }
}
