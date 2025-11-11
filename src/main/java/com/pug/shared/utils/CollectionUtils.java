package com.pug.shared.utils;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class for collection operations.
 */
public final class CollectionUtils {
  /** Private constructor to prevent instantiation. */
  private CollectionUtils() {}

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
