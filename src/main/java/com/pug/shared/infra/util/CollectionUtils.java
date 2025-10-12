package com.pug.shared.infra.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CollectionUtils {
  CollectionUtils() {
    throw new AssertionError("Utility Class");
  }

  public static boolean isNotEmpty(Collection<?> c) {
    return c != null && !c.isEmpty();
  }

  public static boolean isEmpty(Collection<?> c) {
    return c == null || c.isEmpty();
  }

  public static boolean isNotEmpty(Map<?, ?> m) {
    return m != null && !m.isEmpty();
  }

  public static boolean isEmpty(Map<?, ?> m) {
    return m == null || m.isEmpty();
  }

  /** Immutable, typed result instead of Map<Boolean, List<T>> */
  public record Partition<T>(List<T> matched, List<T> unmatched) {
    public Partition {
      matched = List.copyOf(matched);
      unmatched = List.copyOf(unmatched);
    }
  }

  /** Alias name matches JDK terminology; copies to unmodifiable lists. */
  public static <T> Partition<T> partitionBy(Collection<T> list, Predicate<? super T> predicate) {
    Objects.requireNonNull(list, "list");
    Objects.requireNonNull(predicate, "predicate");
    var parts = list.stream().collect(Collectors.partitioningBy(predicate));
    return new Partition<>(parts.get(true), parts.get(false));
  }

  /** Null-safe helper commonly needed in services. */
  public static <T> List<T> nullToEmpty(Collection<T> c) {
    return c == null ? List.of() : List.copyOf(c);
  }
}
