package com.pug.shared.text;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

  /**
   * Trim string. Null-safe.
   *
   * @param s string to trim.
   * @return trimmed string or null if input was null.
   */
  public static String trim(String s) {
    return s == null ? null : s.trim();
  }

  /**
   * Format OffsetDateTime to string using default locale.
   *
   * @param dateTime the date time to format.
   * @return formatted date time string.
   */
  public static String formatDateTime(OffsetDateTime dateTime) {
    return formatDateTime(dateTime, Locale.getDefault());
  }

  /**
   * Format an OffsetDateTime into a human-readable string using the given locale. Null-safe:
   * returns "" if dateTime is null.
   *
   * @param dateTime the date-time to format
   * @param locale the target locale; if null, system default is used
   * @return localized date-time string, or "" if dateTime is null
   */
  public static String formatDateTime(OffsetDateTime dateTime, Locale locale) {
    if (dateTime == null) {
      return "";
    }
    Locale loc = (locale != null) ? locale : Locale.getDefault();
    DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(loc);
    return dateTime.format(fmt);
  }

  /**
   * Format a LocalDate into a human-readable string using the given locale. Null-safe: returns ""
   * if date is null.
   *
   * @param date the date to format
   * @param locale the target locale; if null, system default is used
   * @return localized date string, or "" if date is null
   */
  public static String formatLocalDate(LocalDate date, Locale locale) {
    if (date == null) {
      return "";
    }
    Locale loc = (locale != null) ? locale : Locale.getDefault();
    DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(loc);
    return date.format(fmt);
  }

  /**
   * Format LocalDate to string using default locale.
   *
   * @param date the date to format.
   * @return formatted date string.
   */
  public static String formatLocalDate(LocalDate date) {
    return formatLocalDate(date, Locale.getDefault());
  }
}
