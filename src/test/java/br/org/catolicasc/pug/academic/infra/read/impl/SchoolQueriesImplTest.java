package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SchoolQueriesImplTest {

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
}
