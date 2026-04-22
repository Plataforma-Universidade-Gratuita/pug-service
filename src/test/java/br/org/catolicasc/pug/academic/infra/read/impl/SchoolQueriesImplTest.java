package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.infra.persistence.SchoolEntity;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("SchoolQueriesImpl Coverage")
class SchoolQueriesImplTest extends BaseSearchTest {

  @Inject SchoolQueriesImpl queries;
  @Inject TestDataFactory factory;

  private School school;

  @BeforeEach
  void setup() {
    school = factory.createSchool();
  }

  @Test
  @Transactional
  @DisplayName("Should find SchoolView by ID")
  void shouldFindById() {
    var view = queries.findOptionalById(school.getId());
    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo(school.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent ID")
  void shouldReturnEmptyForNonExistentId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all schools")
  void shouldListAllSchools() {
    assertThat(queries.listAllSchools()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list by IDs")
  void shouldListByIds() {
    var list = queries.listByIds(List.of(school.getId()));
    assertThat(list).hasSize(1);
    assertThat(list.get(0).name()).isEqualTo(school.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null or empty IDs")
  void shouldReturnEmptyListForIds() {
    assertThat(queries.listByIds(null)).isEmpty();
    assertThat(queries.listByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should search schools by name successfully")
  void shouldSearchByNameSuccess() throws Exception {
    syncIndex(SchoolEntity.class);
    var results = queries.searchByName(school.getName().substring(0, 3));
    assertThat(results).anyMatch(v -> v.id().equals(school.getId()));
  }

  @Test
  @DisplayName("Should return empty for non-existent search query")
  void shouldReturnEmptyForNoMatches() throws Exception {
    syncIndex(SchoolEntity.class);
    assertThat(queries.searchByName("NonExistentSchool123")).isEmpty();
  }
}
