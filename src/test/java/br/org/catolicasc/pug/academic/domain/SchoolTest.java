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
    School school = School.factory("School of Engineering");

    assertThat(school.hasFieldErrors()).isFalse();
    assertThat(school.getName()).isEqualTo("School of Engineering");
    assertThat(school.getAuditInfo()).isNotNull();
  }

  @Test
  @DisplayName("Should collect errors for blank name")
  void shouldCollectValidationErrors() {
    School school = School.factory("   ");

    assertThat(school.hasFieldErrors()).isTrue();
    assertThat(school.getFieldErrors()).contains(SharedFieldErrorCodes.INVALID_NAME_BLANK);
  }

  @Nested
  @DisplayName("Behavior Methods")
  class BehaviorTests {

    @Test
    @DisplayName("Should rename school successfully")
    void shouldRename() {
      School school = School.factory("Old Name");
      School renamed = school.rename("New Name");

      assertThat(renamed.getName()).isEqualTo("New Name");
      assertThat(renamed.getId()).isEqualTo(school.getId());
      assertThat(renamed.getAuditInfo().getUpdatedAt())
          .isAfterOrEqualTo(school.getAuditInfo().getCreatedAt());
    }

    @Test
    @DisplayName("Should be idempotent when renaming to the same name")
    void shouldBeIdempotent() {
      School school = School.factory("Same Name");
      School renamed = school.rename("Same Name");

      assertThat(renamed).isSameAs(school);
    }
  }
}
