package com.pug.shared.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

  @Test
  void isEmptyVariants() {
    assertTrue(CollectionUtils.isEmpty((List<?>) null));
    assertTrue(CollectionUtils.isEmpty(List.of()));
    assertFalse(CollectionUtils.isEmpty(List.of(1)));

    assertTrue(CollectionUtils.isEmpty((Map<?, ?>) null));
    assertTrue(CollectionUtils.isEmpty(Map.of()));
    assertFalse(CollectionUtils.isEmpty(Map.of("k", "v")));
  }

  @Test
  void isNotEmptyVariants() {
    assertFalse(CollectionUtils.isNotEmpty((List<?>) null));
    assertFalse(CollectionUtils.isNotEmpty(List.of()));
    assertTrue(CollectionUtils.isNotEmpty(List.of(1)));

    assertFalse(CollectionUtils.isNotEmpty((Map<?, ?>) null));
    assertFalse(CollectionUtils.isNotEmpty(Map.of()));
    assertTrue(CollectionUtils.isNotEmpty(Map.of("k", "v")));
  }

  @Test
  void nullToEmptyReturnsImmutableEmptyOnNull() {
    var out = CollectionUtils.nullToEmpty(null);
    assertNotNull(out);
    assertTrue(out.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> out.add(1));
  }

  @Test
  void nullToEmptyCopiesAndIsUnmodifiable() {
    var src = List.of(1, 2, 3);
    var out = CollectionUtils.nullToEmpty(src);
    assertEquals(src, out);
    assertThrows(UnsupportedOperationException.class, () -> out.add(4));
  }

  @Test
  void partitionByPartitionsAndCopiesToUnmodifiableLists() {
    var src = List.of(1, 2, 3, 4, 5);
    var part = CollectionUtils.partitionBy(src, i -> i % 2 == 0);
    assertEquals(List.of(2, 4), part.matched());
    assertEquals(List.of(1, 3, 5), part.unmatched());
    assertThrows(UnsupportedOperationException.class, () -> part.matched().add(6));
    assertThrows(UnsupportedOperationException.class, () -> part.unmatched().add(7));
  }

  @Test
  void partitionByNullArgsThrow() {
    assertThrows(NullPointerException.class, () -> CollectionUtils.partitionBy(null, x -> true));
    assertThrows(NullPointerException.class, () -> CollectionUtils.partitionBy(List.of(1), null));
  }

  @Test
  void utilityClassConstructorBlocked() {
    var ex = assertThrows(AssertionError.class, CollectionUtils::new);
    assertEquals("Utility Class", ex.getMessage());
  }
}
