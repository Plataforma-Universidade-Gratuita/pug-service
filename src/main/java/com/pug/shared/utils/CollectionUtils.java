package com.pug.shared.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Utility class for collection operations. */
public final class CollectionUtils {
  /** Private constructor to prevent instantiation. */
  private CollectionUtils() {}

  /**
   * Check if an Iterable is null or empty.
   *
   * @param it the iterable.
   * @return true if null or empty, false otherwise.
   */
  public static boolean isEmpty(Iterable<?> it) {
    return it == null || !it.iterator().hasNext();
  }

  /**
   * Check if a List is null or empty.
   *
   * @param list the list.
   * @return true if null or empty, false otherwise.
   */
  public static boolean isEmpty(List<?> list) {
    return list == null || list.isEmpty();
  }

  /**
   * Check if a Map is null or empty.
   *
   * @param map the map.
   * @return true if null or empty, false otherwise.
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  /**
   * Convert Iterable to Stream.
   *
   * @param it the iterable.
   * @param <T> the type of elements.
   * @return the stream.
   */
  public static <T> Stream<T> toStream(Iterable<T> it) {
    return it == null ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
