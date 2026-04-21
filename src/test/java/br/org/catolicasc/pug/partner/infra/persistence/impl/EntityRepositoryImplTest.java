package br.org.catolicasc.pug.partner.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EntityRepositoryImplTest {

  @Inject EntityRepositoryImpl repository;
  @Inject TestDataFactory factory;

  @Test
  @Transactional
  @DisplayName("Should persist and find Entity")
  void shouldPersistAndFind() {
    Entity entity = factory.createEntity(factory.getAnyCity());

    Optional<Entity> found = repository.findOptionalById(entity.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(entity.getName());
  }
}
