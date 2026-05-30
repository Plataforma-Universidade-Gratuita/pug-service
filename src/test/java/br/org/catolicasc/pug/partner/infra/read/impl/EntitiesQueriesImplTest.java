package br.org.catolicasc.pug.partner.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.infra.read.dtos.EntityComplexSearchView;
import br.org.catolicasc.pug.partner.service.dtos.entities.EntityComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntitiesQueriesImpl Coverage")
class EntitiesQueriesImplTest extends BaseSearchTest {

  @Inject EntitiesQueriesImpl queries;
  @Inject TestDataFactory factory;

  @Test
  @TestTransaction
  @DisplayName("Should retrieve EntityView by ID")
  void shouldFindById() throws Exception {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    assertThat(queries.findOptionalById(entity.getId())).isPresent();
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @TestTransaction
  @DisplayName("Should list entities by IDs and list all")
  void listOperations() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    assertThat(queries.listAllByIds(List.of(entity.getId()))).hasSize(1);
    assertThat(queries.listAllEntities()).isNotEmpty();
  }

  @Test
  @TestTransaction
  @DisplayName("Should execute entity complex search with pagination")
  void searchSuccess() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    var result =
        queries.search(
            new PageQuery(0, 25),
            new EntityComplexSearchCriteria(
                entity.getName().substring(0, 3), null, null, null, null, null));

    assertThat(result.content())
        .extracting(EntityComplexSearchView::id)
        .contains(entity.getId());
  }

  @Test
  @TestTransaction
  @DisplayName("Should search entities by timestamps")
  void searchByTimestampSuccess() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    OffsetDateTime center = entity.getAuditInfo().getCreatedAt();
    var result =
        queries.search(
            new PageQuery(0, 1),
            new EntityComplexSearchCriteria(
                null, null, null, null, center.minusSeconds(1), center.plusSeconds(1)));

    assertThat(result.content())
        .extracting(EntityComplexSearchView::id)
        .contains(entity.getId());
  }

  @Test
  @TestTransaction
  @DisplayName("Should return full entity views when fetch-all sentinel is requested")
  void searchFetchAllSuccess() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    em.flush();

    var result =
        queries.search(
            new PageQuery(0, 1),
            new EntityComplexSearchCriteria(entity.getName().substring(0, 2), null, null, null, null, null));

    assertThat(result.content())
        .extracting(EntityComplexSearchView::id)
        .contains(entity.getId());
    assertThat(result.totalElements()).isGreaterThanOrEqualTo(1);
  }
}
