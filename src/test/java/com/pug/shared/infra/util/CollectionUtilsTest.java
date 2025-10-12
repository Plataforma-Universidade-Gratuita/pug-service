package com.pug.shared.infra.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

  @Test
  void constructorIsInaccessibleAndThrows() {
    var ex = assertThrows(AssertionError.class, CollectionUtils::new);
    assertEquals("Utility Class", ex.getMessage());
  }

  @Test
  void isEmptyAndIsNotEmptyForCollections() {
    assertTrue(CollectionUtils.isEmpty((Collection<?>) null));
    assertTrue(CollectionUtils.isEmpty(List.of()));
    assertFalse(CollectionUtils.isEmpty(List.of(1)));

    assertFalse(CollectionUtils.isNotEmpty((Collection<?>) null));
    assertFalse(CollectionUtils.isNotEmpty(List.of()));
    assertTrue(CollectionUtils.isNotEmpty(List.of(1)));
  }

  @Test
  void isEmptyAndIsNotEmptyForMaps() {
    assertTrue(CollectionUtils.isEmpty((Map<?, ?>) null));
    assertTrue(CollectionUtils.isEmpty(Map.of()));
    assertFalse(CollectionUtils.isEmpty(Map.of("k", "v")));

    assertFalse(CollectionUtils.isNotEmpty((Map<?, ?>) null));
    assertFalse(CollectionUtils.isNotEmpty(Map.of()));
    assertTrue(CollectionUtils.isNotEmpty(Map.of("k", "v")));
  }

  @Test
  void partitionBySplitsAndIsImmutable() {
    var list = List.of(1, 2, 3, 4, 5);
    var part = CollectionUtils.partitionBy(list, x -> x % 2 == 0);

    assertEquals(List.of(2, 4), part.matched());
    assertEquals(List.of(1, 3, 5), part.unmatched());

    assertThrows(UnsupportedOperationException.class, () -> part.matched().add(6));
    assertThrows(UnsupportedOperationException.class, () -> part.unmatched().add(7));
  }

  @Test
  void partitionByNullGuards() {
    assertThrows(NullPointerException.class, () -> CollectionUtils.partitionBy(null, x -> true));
    assertThrows(NullPointerException.class, () -> CollectionUtils.partitionBy(List.of(), null));
  }

  @Test
  void nullToEmptyReturnsUnmodifiableCopy() {
    var empty = CollectionUtils.nullToEmpty(null);
    assertTrue(empty.isEmpty());
    assertThrows(UnsupportedOperationException.class, () -> empty.add(1));

    var src = new ArrayList<>(List.of(1, 2));
    var copy = CollectionUtils.nullToEmpty(src);
    assertEquals(List.of(1, 2), copy);
    src.add(3);
    assertEquals(List.of(1, 2), copy); // defensive copy
    assertThrows(UnsupportedOperationException.class, () -> copy.add(3));
  }
}
