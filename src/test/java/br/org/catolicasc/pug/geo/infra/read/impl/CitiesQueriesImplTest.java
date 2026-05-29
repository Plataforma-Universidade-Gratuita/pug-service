package br.org.catolicasc.pug.geo.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.geo.infra.persistence.CityEntity;
import br.org.catolicasc.pug.geo.infra.read.dtos.CityView;
import br.org.catolicasc.pug.geo.service.dtos.CityComplexSearchCriteria;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CitiesQueriesImpl Coverage")
class CitiesQueriesImplTest extends BaseSearchTest {

  @Inject CitiesQueriesImpl queries;

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

  @Test
  @DisplayName("Should return empty list when searching invalid/non-existent IDs")
  void listAllByIdsInvalid() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of(UuidCreator.getTimeOrderedEpoch()))).isEmpty();
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
    assertThat(cities).hasSizeGreaterThan(200);
  }

  @Test
  @DisplayName("Should search cities by name successfully")
  void searchByNameSuccess() {
    var result = queries.search(new PageQuery(0, 10), new CityComplexSearchCriteria("Joinville"));
    assertThat(result.content()).anyMatch(c -> c.name().equals("Joinville"));
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @DisplayName("Should return paginated city list when no name filter is provided")
  void shouldReturnPaginatedListWithoutNameFilter() {
    var result = queries.search(new PageQuery(0, 5), new CityComplexSearchCriteria(null));
    assertThat(result.content()).hasSizeLessThanOrEqualTo(5);
    assertThat(result.totalElements()).isGreaterThan(200);
  }

  @Test
  @DisplayName("Should return paginated city list when search criteria is null")
  void shouldHandleNullSearchCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);
    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void shouldFetchAllWhenPageSizeIsOne() {
    var result = queries.search(new PageQuery(3, 1), new CityComplexSearchCriteria("Join"));
    assertThat(result.page()).isZero();
    assertThat(result.totalPages()).isLessThanOrEqualTo(1);
    assertThat(result.content().size()).isEqualTo(result.totalElements());
    assertThat(result.size()).isEqualTo(Math.max((int) result.totalElements(), 1));
  }
}
