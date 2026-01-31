package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.PresenterUtils;
import com.pug.shared.utils.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Mapper class for converting StudentView to StudentResponse.
 */
public final class StudentPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private StudentPresenter() {
  }

  /**
   * Converts a StudentView to a StudentResponse.
   *
   * @param v      the StudentView to convert
   * @param locale the locale for localization
   * @param i18n   the internationalization service
   * @return the converted StudentResponse
   */
  public static StudentResponse toResponse(StudentView v, Locale locale, I18n i18n) {
    if (v == null) {
      return null;
    }

    BigDecimal missingHours = BigDecimal.ZERO;
    if (v.requiredHours() != null && v.completedHours() != null) {
      missingHours = v.requiredHours().subtract(v.completedHours());
    }

    String startDateFormatted = StringUtils.toStringFormatted(v.startDate(), locale);
    String dueDateFormatted = StringUtils.toStringFormatted(v.dueDate(), locale);

    long remainingDays = 0;
    String remainingDaysFormatted = "";
    if (v.dueDate() != null) {
      remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), v.dueDate());
      remainingDaysFormatted = PresenterUtils.formatRemainingDays(v.dueDate(), locale, i18n);
    }

    return new StudentResponse(
            AccountPresenter.toResponse(v.account(), locale, i18n),
            v.academicRegistration(),
            v.campus(),
            CoursePresenter.toResponse(v.course()),
            v.requiredHours(),
            v.completedHours(),
            missingHours,
            v.startDate(),
            startDateFormatted,
            v.dueDate(),
            dueDateFormatted,
            Math.max(0, remainingDays),
            remainingDaysFormatted);
  }
}