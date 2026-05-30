package br.org.catolicasc.pug.academic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("School Aggregate Tests")
class SchoolTest {

  @Test
  @DisplayName("Should create valid School")
  void shouldCreateSchool() {
    School areaOfExpertise = School.factory("School of Engineering");

    assertThat(areaOfExpertise.hasFieldErrors()).isFalse();
    assertThat(areaOfExpertise.getName()).isEqualTo("School of Engineering");
    assertThat(areaOfExpertise.getAuditInfo()).isNotNull();
  }

  @Test
  @DisplayName("Should collect errors for blank name")
  void shouldCollectValidationErrors() {
    School areaOfExpertise = School.factory("   ");

    assertThat(areaOfExpertise.hasFieldErrors()).isTrue();
    assertThat(areaOfExpertise.getFieldErrors()).contains(SharedFieldErrorCodes.INVALID_NAME_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename areaOfExpertise successfully")
    void shouldRename() {
      School areaOfExpertise = School.factory("Old Name");
      School renamed = areaOfExpertise.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getId()).isEqualTo(areaOfExpertise.getId());
      assertThat(renamed.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(areaOfExpertise.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should be idempotent when renaming to the same name")
    void shouldBeIdempotent() {
      School areaOfExpertise = School.factory("Same Name");
      School renamed = areaOfExpertise.rename("Same Name");

      assertThat(renamed).isSameAs(areaOfExpertise);
    }
  }
}
