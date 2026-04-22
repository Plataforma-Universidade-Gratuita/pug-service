package br.org.catolicasc.pug.partner.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityView;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntityQueriesImpl Coverage")
class EntityQueriesImplTest extends BaseSearchTest {

  @Inject EntityQueriesImpl queries;
  @Inject TestDataFactory factory;

  @Test
  @TestTransaction
  @DisplayName("Should retrieve EntityView by ID and CNPJ")
  void shouldFindByIdAndCnpj() throws Exception {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    assertThat(queries.findOptionalById(entity.getId())).isPresent();
    assertThat(queries.findOptionalByCnpj(entity.getCnpj().getValue())).isPresent();

    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
    assertThat(queries.findOptionalByCnpj("00000000000000")).isEmpty();
  }

  @Test
  @TestTransaction
  @DisplayName("Should list entities by CityID and list all")
  void listOperations() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    assertThat(queries.listAllByCityId(entity.getCityId())).hasSize(1);
    assertThat(queries.listAllCityIds()).contains(entity.getCityId());
    assertThat(queries.listAllEntities()).isNotEmpty();
  }

  @Test
  @DisplayName("Should search entities by name using Hibernate Search")
  void searchByName() throws Exception {
    Entity[] entity = new Entity[1];

    runInTransaction(
        () -> {
          entity[0] = factory.createEntity(factory.getAnyCity());
        });
    syncIndex(EntityEntity.class);
    List<EntityView> found = queries.searchByName(entity[0].getName().substring(0, 3));

    assertThat(found).anyMatch(v -> v.name().equals(entity[0].getName()));
    assertThat(queries.searchByName("NonExistent")).isEmpty();
  }
}
