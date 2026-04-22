package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SchoolProcessor Coverage")
class SchoolProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    School school = SchoolProcessor.processCreateInput("Engineering");

    assertThat(school.hasFieldErrors()).isFalse();
    assertThat(school.getName()).isEqualTo("Engineering");
    assertThat(school.getId()).isNotNull();
  }

  @Test
  @DisplayName("Should collect errors for blank name")
  void shouldCollectErrorsForBlankName() {
    School school = SchoolProcessor.processCreateInput("   ");

    assertThat(school.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should update name via processUpdateInput")
  void shouldUpdateName() {
    School existing = School.factory("Old Name");
    School updated = SchoolProcessor.processUpdateInput(existing, "New Name");

    assertThat(updated.getName()).isEqualTo("New Name");
  }

  @Test
  @DisplayName("Should skip update if name is null")
  void shouldSkipNullName() {
    School existing = School.factory("Keep Me");
    School updated = SchoolProcessor.processUpdateInput(existing, null);

    assertThat(updated.getName()).isEqualTo("Keep Me");
  }

  @Test
  @DisplayName("Should skip update if name is empty")
  void shouldSkipEmptyName() {
    School existing = School.factory("Keep Me");
    School updated = SchoolProcessor.processUpdateInput(existing, "");

    assertThat(updated.getName()).isEqualTo("Keep Me");
  }
}
