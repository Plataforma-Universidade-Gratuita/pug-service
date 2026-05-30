package br.org.catolicasc.pug.academic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AreaOfExpertise Aggregate Tests")
class AreaOfExpertiseTest {

  @Test
  @DisplayName("Should create valid AreaOfExpertise")
  void shouldCreateAreaOfExpertise() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Software Engineering");

    assertThat(areaOfExpertise.hasFieldErrors()).isFalse();
    assertThat(areaOfExpertise.getId()).isNotNull();
    assertThat(areaOfExpertise.getName()).isEqualTo("Software Engineering");
    assertThat(areaOfExpertise.getAuditInfo()).isNotNull();
  }

  @Test
  @DisplayName("Should trim name on creation")
  void shouldTrimName() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("  Engineering  ");

    assertThat(areaOfExpertise.hasFieldErrors()).isFalse();
    assertThat(areaOfExpertise.getName()).isEqualTo("Engineering");
  }

  @Test
  @DisplayName("Should collect errors when name is blank")
  void shouldCollectValidationErrors() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("   ");

    assertThat(areaOfExpertise.hasFieldErrors()).isTrue();
    assertThat(areaOfExpertise.getFieldErrors()).contains(SharedFieldErrorCodes.INVALID_NAME_BLANK);
  }

  @Test
  @DisplayName("Should collect errors when name is too long")
  void shouldRejectTooLongName() {
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("A".repeat(151));

    assertThat(areaOfExpertise.hasFieldErrors()).isTrue();
    assertThat(areaOfExpertise.getFieldErrors())
        .contains(SharedFieldErrorCodes.INVALID_NAME_TOO_LONG);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename area of expertise successfully")
    void shouldRename() {
      AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Original Name");

      AreaOfExpertise renamed = areaOfExpertise.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getId()).isEqualTo(areaOfExpertise.getId());
      assertThat(renamed.getAuditInfo().getCreatedAt())
          .isEqualTo(areaOfExpertise.getAuditInfo().getCreatedAt());
      assertThat(renamed.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(areaOfExpertise.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should return same instance when renaming to same name")
    void shouldReturnSameInstanceWhenNameIsUnchanged() {
      AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Same Name");

      AreaOfExpertise renamed = areaOfExpertise.rename("Same Name");

      assertThat(renamed).isSameAs(areaOfExpertise);
    }

    @Test
    @DisplayName("Should trim name when renaming")
    void shouldTrimWhenRenaming() {
      AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Original");

      AreaOfExpertise renamed = areaOfExpertise.rename("  Updated  ");

      assertThat(renamed.getName()).isEqualTo("Updated");
    }
  }
}
