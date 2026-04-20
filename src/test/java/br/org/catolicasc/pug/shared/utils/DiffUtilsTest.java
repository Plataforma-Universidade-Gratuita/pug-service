package br.org.catolicasc.pug.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.shared.infra.audit.FieldChange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DiffUtils Tests")
class DiffUtilsTest {

  // Helper class for testing reflection
  @Getter
  @AllArgsConstructor
  static class TestDomain {
    private String name;
    private Integer age;
    private String email; // Should be ignored
  }

  @Nested
  @DisplayName("Method: diff")
  class DiffTests {

    @Test
    @DisplayName("Should detect changes in allowed fields and ignore restricted ones")
    void shouldDetectChanges() {
      TestDomain oldObj = new TestDomain("Original", 20, "old@test.com");
      TestDomain newObj = new TestDomain("Updated", 25, "new@test.com");

      var result = DiffUtils.diff(oldObj, newObj);

      // name and age should be detected
      assertThat(result).hasSize(2);
      assertThat(result.get("name")).isEqualTo(new FieldChange("Original", "Updated"));
      assertThat(result.get("age")).isEqualTo(new FieldChange(20, 25));

      // email is in Ignored enum, should not be present
      assertThat(result).doesNotContainKey("email");
    }

    @Test
    @DisplayName("Should return empty map if objects are identical")
    void shouldReturnEmptyForIdentical() {
      TestDomain obj = new TestDomain("Same", 20, "same@test.com");
      assertThat(DiffUtils.diff(obj, obj)).isEmpty();
    }

    @Test
    @DisplayName("Should return empty map if input is null")
    void shouldHandleNulls() {
      assertThat(DiffUtils.diff(null, new TestDomain("A", 1, "a@t.com"))).isEmpty();
      assertThat(DiffUtils.diff(new TestDomain("A", 1, "a@t.com"), null)).isEmpty();
    }
  }
}
