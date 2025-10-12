package com.pug.shared.infra.util;

import java.text.Normalizer;
import java.util.Locale;

public final class TextSearchUtils {
  private TextSearchUtils() {}

  public static String normalize(String s) {
    if (s == null) return null;
    String n = Normalizer.normalize(s, Normalizer.Form.NFD);
    return n.replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
  }

  public static String likeParam(String pattern) {
    return "%" + normalize(pattern) + "%";
  }
}
