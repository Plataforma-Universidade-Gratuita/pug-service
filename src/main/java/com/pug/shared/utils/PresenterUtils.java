package com.pug.shared.utils;

import java.util.List;
import java.util.Locale;

/**
 * Utility class for presenter-related functions.
 */
public final class PresenterUtils {
  /**
   * Private constructor to prevent instantiation.
   */
  private PresenterUtils() {
  }

  /**
   * Picks the most suitable locale from a list of acceptable locales.
   * If the list is null or empty, defaults to "pt-BR".
   *
   * @param acceptable List of acceptable locales.
   * @return The selected locale.
   */
  public static Locale pickLocale(List<Locale> acceptable) {
    return CollectionUtils.isEmpty(acceptable)
            ? Locale.forLanguageTag("pt-BR")
            : acceptable.getFirst();
  }
}