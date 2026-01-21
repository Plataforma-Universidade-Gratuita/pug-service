package com.pug.shared.utils;

import com.pug.shared.i18n.I18n;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

/** Utility class for presenter-related functions. */
public final class PresenterUtils {
  /** Private constructor to prevent instantiation. */
  private PresenterUtils() {}

  /**
   * Picks the most suitable locale from a list of acceptable locales. If the list is null or empty,
   * defaults to "pt-BR".
   *
   * @param acceptable List of acceptable locales.
   * @return The selected locale.
   */
  public static Locale pickLocale(List<Locale> acceptable) {
    return CollectionUtils.isEmpty(acceptable)
        ? Locale.forLanguageTag("pt-BR")
        : acceptable.getFirst();
  }

  /**
   * Formats the number of remaining days until a due date into a human-readable, localized string.
   *
   * @param dueDate the due date.
   * @param locale the target locale.
   * @param i18n the internationalization service.
   * @return a localized string representing the remaining days (e.g., "Hoje", "Amanhã", "X dias
   *     restantes", "Atrasado").
   */
  public static String formatRemainingDays(LocalDate dueDate, Locale locale, I18n i18n) {
    if (dueDate == null) {
      return "";
    }

    long remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

    String formattedString;
    if (remainingDays < 0) {
      formattedString =
          i18n.translation("academic.student.days.overdue", locale, Math.abs(remainingDays));
    } else if (remainingDays == 0) {
      formattedString = i18n.translation("academic.student.days.today", locale);
    } else if (remainingDays == 1) {
      formattedString = i18n.translation("academic.student.days.tomorrow", locale);
    } else {
      formattedString = i18n.translation("academic.student.days.remaining", locale, remainingDays);
    }
    return formattedString;
  }
}
