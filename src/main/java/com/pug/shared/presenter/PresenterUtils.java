package com.pug.shared.presenter;

import java.util.List;
import java.util.Locale;

/** Utility class for presenter-related functions. */
public class PresenterUtils {
  /**
   * Picks the most suitable locale from a list of acceptable locales. If the list is null or empty,
   * defaults to "pt-BR".
   *
   * @param acceptable List of acceptable locales.
   * @return The selected locale.
   */
  public static Locale pickLocale(List<Locale> acceptable) {
    return acceptable == null || acceptable.isEmpty()
        ? Locale.forLanguageTag("pt-BR")
        : acceptable.getFirst();
  }
}
