package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.dtos.CampusResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/** Mapper class for converting StudentView to StudentResponse. */
public final class StudentPresenter {
  /** Private constructor to prevent instantiation. */
  private StudentPresenter() {}

  /**
   * Converts a StudentView to a StudentResponse.
   *
   * @param v the StudentView to convert
   * @param locale the locale for localization
   * @param i18n the internationalization service
   * @return the converted StudentResponse
   */
  public static StudentResponse toResponse(StudentView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String startDateFormatted = StringUtils.toStringFormatted(v.startDate(), locale);
    String dueDateFormatted = StringUtils.toStringFormatted(v.dueDate(), locale);

    BigDecimal missingHours = BigDecimal.ZERO;
    if (v.requiredHours() != null && !v.concluded()) {
      missingHours = v.requiredHours().subtract(BigDecimal.ZERO);
    }

    long remainingDays = 0;
    String remainingDaysFormatted = "";
    if (v.dueDate() != null) {
      remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), v.dueDate());
      remainingDaysFormatted = formatRemainingDays(v.dueDate(), locale, i18n);
    }

    CampusResponse campus = SharedDataPresenter.createCampusResponse(v.campus(), locale, i18n);
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new StudentResponse(
        AccountPresenter.toResponse(v.account(), locale, i18n),
        v.academicRegistration(),
        campus,
        CoursePresenter.toResponse(v.course(), locale),
        v.requiredHours(),
        BigDecimal.ZERO,
        missingHours,
        v.startDate(),
        startDateFormatted,
        v.dueDate(),
        dueDateFormatted,
        remainingDays,
        remainingDaysFormatted,
        auditInfo);
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
  private static String formatRemainingDays(LocalDate dueDate, Locale locale, I18n i18n) {
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
