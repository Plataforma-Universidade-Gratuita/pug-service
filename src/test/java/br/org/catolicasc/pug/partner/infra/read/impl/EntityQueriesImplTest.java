package br.org.catolicasc.pug.partner.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EntityQueriesImplTest {

  @Inject EntityQueriesImpl queries;
  @Inject TestDataFactory factory;

  @Test
  @Transactional
  @DisplayName("Should retrieve EntityView by ID")
  void shouldFindById() {
    Entity entity = factory.createEntity(factory.getAnyCity());

    var view = queries.findOptionalById(entity.getId());
    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo(entity.getName());
  }
}
