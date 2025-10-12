package com.pug.shared.infra.util;

public final class CpfUtils {
  private CpfUtils() {}

  public static String onlyDigits(String s) {
    return s == null ? null : s.replaceAll("\\D", "");
  }

  public static String sanitize(String s) {
    return onlyDigits(s);
  }
}
