package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseComplexSearchCriteria;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AreasOfExpertiseQueriesImpl Coverage")
class AreasOfExpertiseQueriesImplTest extends BaseSearchTest {

  @Inject AreasOfExpertiseQueriesImpl queries;
  @Inject TestDataFactory factory;

  private AreaOfExpertise area;

  @BeforeEach
  void setup() {
    area = factory.createAreaOfExpertise();
  }

  @Test
  @Transactional
  @DisplayName("Should find area-of-expertise view by ID")
  void findByIdSuccess() {
    var view = queries.findOptionalById(area.getId());
    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo(area.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void findByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all areas of expertise")
  void listAllViews() {
    assertThat(queries.listAllViews()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list areas of expertise by IDs")
  void listAllByIds() {
    var list = queries.listAllByIds(List.of(area.getId()));
    assertThat(list).hasSize(1);
    assertThat(list.get(0).id()).isEqualTo(area.getId());
  }

  @Test
  @DisplayName("Should search areas of expertise by name successfully")
  void searchByNameSuccess() {
    String searchKey = area.getName().substring(0, 3);
    var result =
        queries.search(new PageQuery(0, 10), new AreaOfExpertiseComplexSearchCriteria(searchKey));
    assertThat(result.content()).anyMatch(v -> v.id().equals(area.getId()));
    assertThat(result.page()).isZero();
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void fetchAllSuccess() {
    String searchKey = area.getName().substring(0, 3);
    var result =
        queries.search(new PageQuery(2, 1), new AreaOfExpertiseComplexSearchCriteria(searchKey));
    assertThat(result.page()).isZero();
    assertThat(result.content().size()).isEqualTo(result.totalElements());
  }
}
