package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.academic.presenter.dtos.StudentResponse;
import com.pug.identity.presenter.mappers.UserPresenter;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    BigDecimal missingHours = v.requiredHours().subtract(v.completedHours());
    String startDateLabel = StringUtils.toStringFormatted(v.startDate(), locale);
    String dueDateLabel = StringUtils.toStringFormatted(v.dueDate(), locale);
    LocalDate remainingDays =
        v.dueDate().isAfter(LocalDate.now())
            ? v.dueDate().minusDays(LocalDate.now().toEpochDay())
            : LocalDate.ofEpochDay(0);
    String remainingDaysLabel = StringUtils.toStringFormatted(remainingDays, locale);

    return new StudentResponse(
        UserPresenter.toResponse(v.user(), locale, i18n),
        v.academicRegistration(),
        v.campus(),
        CoursePresenter.toResponse(v.course()),
        v.requiredHours(),
        v.completedHours(),
        missingHours,
        v.startDate(),
        startDateLabel,
        v.dueDate(),
        dueDateLabel,
        remainingDays,
        remainingDaysLabel);
  }
}
