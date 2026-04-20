package br.org.catolicasc.pug.shared.utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.regex.Pattern;

/** Utility class for string manipulation, including normalization and formatting. */
public final class StringUtils {
  private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

  /** Private constructor to prevent instantiation. */
  private StringUtils() {}

  /**
   * Converts a string to lowercase, removes accents (diacritics), and trims leading/trailing
   * whitespace. Null-safe: returns an empty string if the input is null.
   *
   * @param s the string to fold.
   * @return the folded string.
   */
  public static String fold(String s) {
    if (s == null) {
      return "";
    }
    String n = Normalizer.normalize(s, Normalizer.Form.NFD);
    return DIACRITICS.matcher(n).replaceAll("").toLowerCase(Locale.ROOT).trim();
  }

  /**
   * Checks if a string is null or blank (empty or contains only whitespace characters).
   *
   * @param s the string to check.
   * @return true if the string is null or blank, false otherwise.
   */
  public static boolean isEmpty(String s) {
    return s == null || s.isBlank();
  }

  /**
   * Checks if a string is not null and contains non-whitespace characters.
   *
   * @param s the string to check.
   * @return true if the string is not null and not blank, false otherwise.
   */
  public static boolean isNotEmpty(String s) {
    return !isEmpty(s);
  }

  /**
   * Trims leading and trailing whitespace from a string. Null-safe.
   *
   * @param s the string to trim.
   * @return the trimmed string, or null if the input was null.
   */
  public static String trim(String s) {
    return s == null ? null : s.trim();
  }

  /**
   * Formats an {@code OffsetDateTime} into a human-readable string using the specified locale.
   * Null-safe: returns an empty string if {@code dateTime} is null.
   *
   * @param dateTime the date-time to format.
   * @param locale the target locale; if null, the system default is used.
   * @return a localized date-time string, or an empty string if {@code dateTime} is null.
   */
  public static String toStringFormatted(OffsetDateTime dateTime, Locale locale) {
    if (dateTime == null) {
      return "";
    }
    Locale loc = (locale != null) ? locale : Locale.getDefault();
    DateTimeFormatter fmt =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(loc);
    return dateTime.format(fmt);
  }

  /**
   * Formats a {@code LocalDate} into a human-readable string using the specified locale. Null-safe:
   * returns an empty string if {@code date} is null.
   *
   * @param date the date to format.
   * @param locale the target locale; if null, the system default is used.
   * @return a localized date string, or an empty string if {@code date} is null.
   */
  public static String toStringFormatted(LocalDate date, Locale locale) {
    if (date == null) {
      return "";
    }
    Locale loc = (locale != null) ? locale : Locale.getDefault();
    DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(loc);
    return date.format(fmt);
  }
}
