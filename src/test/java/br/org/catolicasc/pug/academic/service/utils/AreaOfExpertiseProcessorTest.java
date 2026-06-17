package br.org.catolicasc.pug.academic.service.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AreaOfExpertiseProcessor Coverage")
class AreaOfExpertiseProcessorTest {

  @Test
  @DisplayName("Should process create input successfully")
  void shouldProcessCreateInput() {
    AreaOfExpertise areaOfExpertise =
        AreaOfExpertiseProcessor.processCreateInput("Computer Science");

    assertThat(areaOfExpertise.hasFieldErrors()).isFalse();
    assertThat(areaOfExpertise.getName()).isEqualTo("Computer Science");
  }

  @Test
  @DisplayName("Should collect errors for blank create input")
  void shouldCollectErrorsForBlankCreateInput() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertiseProcessor.processCreateInput("");

    assertThat(areaOfExpertise.hasFieldErrors()).isTrue();
  }

  @Test
  @DisplayName("Should update name via processUpdateInput")
  void shouldUpdateName() {
    AreaOfExpertise existing = AreaOfExpertise.factory("Old Name");

    AreaOfExpertise updated = AreaOfExpertiseProcessor.processUpdateInput(existing, "New Name");

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getId()).isEqualTo(existing.getId());
  }

  @Test
  @DisplayName("Should skip update when name is null or blank")
  void shouldSkipNullOrBlankNameUpdates() {
    AreaOfExpertise existing = AreaOfExpertise.factory("Original");

    assertThat(AreaOfExpertiseProcessor.processUpdateInput(existing, null)).isSameAs(existing);
    assertThat(AreaOfExpertiseProcessor.processUpdateInput(existing, "   ")).isSameAs(existing);
  }
}
