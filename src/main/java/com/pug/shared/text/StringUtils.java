package com.pug.shared.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Utility class for text normalization. */
public final class StringUtils {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  private StringUtils() {}

  /** Lowercase + remove accents + trim. Null-safe. */
  public static String fold(String s) {
    if (s == null) {
      return "";
    }
    ;
    String n = Normalizer.normalize(s, Normalizer.Form.NFD);
    return DIACRITICS.matcher(n).replaceAll("").toLowerCase(Locale.ROOT).trim();
  }

  /**
   * Check if string is null or blank.
   *
   * @param s string to check.
   * @return true if null or blank.
   */
  public static boolean isEmpty(String s) {
    return s == null || s.isBlank();
  }
}
