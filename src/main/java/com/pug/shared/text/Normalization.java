package com.pug.shared.text;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for text normalization.
 */
public final class Normalization {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  private Normalization() {
  }

  /**
   * Lowercase + remove accents + trim. Null-safe.
   */
  public static String fold(String s) {
    if (s == null) {
      return "";
    }
    ;
    String n = Normalizer.normalize(s, Normalizer.Form.NFD);
    return DIACRITICS.matcher(n).replaceAll("").toLowerCase(Locale.ROOT).trim();
  }
}
