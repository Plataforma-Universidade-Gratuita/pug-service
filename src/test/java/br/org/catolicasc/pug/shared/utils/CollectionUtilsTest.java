package br.org.catolicasc.pug.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CollectionUtils Tests")
class CollectionUtilsTest {

  @Nested
  @DisplayName("Method: isEmpty")
  class IsEmptyTests {
    @Test
    @DisplayName("Should return true for null/empty Iterables")
    void shouldReturnTrueForEmptyIterables() {
      assertThat(CollectionUtils.isEmpty((Iterable<?>) null)).isTrue();
      assertThat(CollectionUtils.isEmpty(Collections.emptyList())).isTrue();
    }

    @Test
    @DisplayName("Should return true for null/empty Lists/Maps")
    void shouldReturnTrueForCollections() {
      assertThat(CollectionUtils.isEmpty((List<?>) null)).isTrue();
      assertThat(CollectionUtils.isEmpty(Collections.emptyList())).isTrue();
      assertThat(CollectionUtils.isEmpty((Map<?, ?>) null)).isTrue();
      assertThat(CollectionUtils.isEmpty(Collections.emptyMap())).isTrue();
    }

    @Test
    @DisplayName("Should return false for non-empty collections")
    void shouldReturnFalseForContent() {
      assertThat(CollectionUtils.isEmpty(List.of("item"))).isFalse();
      assertThat(CollectionUtils.isEmpty(Map.of("key", "val"))).isFalse();
    }
  }

  @Nested
  @DisplayName("Method: isNotEmpty")
  class IsNotEmptyTests {
    @Test
    @DisplayName("Should return true for non-empty collections")
    void shouldReturnTrueForContent() {
      assertThat(CollectionUtils.isNotEmpty(List.of("item"))).isTrue();
      assertThat(CollectionUtils.isNotEmpty(Map.of("key", "val"))).isTrue();
    }

    @Test
    @DisplayName("Should return false for empty/null collections")
    void shouldReturnFalseForEmpty() {
      assertThat(CollectionUtils.isNotEmpty((List<?>) null)).isFalse();
      assertThat(CollectionUtils.isNotEmpty(Collections.emptyList())).isFalse();
      assertThat(CollectionUtils.isNotEmpty((Map<?, ?>) null)).isFalse();
    }
  }

  @Nested
  @DisplayName("Method: toStream")
  class ToStreamTests {
    @Test
    @DisplayName("Should convert list to stream successfully")
    void shouldConvertList() {
      List<String> list = List.of("a", "b");
      assertThat(CollectionUtils.toStream(list)).containsExactly("a", "b");
    }

    @Test
    @DisplayName("Should return empty stream for null input")
    void shouldReturnEmptyForNull() {
      assertThat(CollectionUtils.toStream(null)).isEmpty();
    }
  }
}
