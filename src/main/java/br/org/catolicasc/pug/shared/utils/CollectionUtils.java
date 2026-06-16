package br.org.catolicasc.pug.shared.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Utility class for collection operations. */
public final class CollectionUtils {

  private CollectionUtils() {}

  /**
   * Checks if an {@code Iterable} is null or empty.
   *
   * @param it the iterable.
   * @return true if the iterable is null or empty, false otherwise.
   */
  public static boolean isEmpty(Iterable<?> it) {
    return it == null || !it.iterator().hasNext();
  }

  /**
   * Checks if a {@code List} is null or empty.
   *
   * @param list the list.
   * @return true if the list is null or empty, false otherwise.
   */
  public static boolean isEmpty(List<?> list) {
    return list == null || list.isEmpty();
  }

  /**
   * Checks if a {@code Map} is null or empty.
   *
   * @param map the map.
   * @return true if the map is null or empty, false otherwise.
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  /**
   * Checks if an {@code Iterable} is not null and not empty.
   *
   * @param it the iterable.
   * @return true if the iterable is not null and not empty, false otherwise.
   */
  public static boolean isNotEmpty(Iterable<?> it) {
    return !isEmpty(it);
  }

  /**
   * Checks if a {@code List} is not null and not empty.
   *
   * @param list the list.
   * @return true if the list is not null and not empty, false otherwise.
   */
  public static boolean isNotEmpty(List<?> list) {
    return !isEmpty(list);
  }

  /**
   * Checks if a {@code Map} is not null and not empty.
   *
   * @param map the map.
   * @return true if the map is not null and not empty, false otherwise.
   */
  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }

  /**
   * Converts an {@code Iterable} to a {@code Stream}.
   *
   * @param it the iterable.
   * @param <T> the type of elements in the iterable.
   * @return a stream containing the elements of the iterable, or an empty stream if the iterable is
   *     null.
   */
  public static <T> Stream<T> toStream(Iterable<T> it) {
    return it == null ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
