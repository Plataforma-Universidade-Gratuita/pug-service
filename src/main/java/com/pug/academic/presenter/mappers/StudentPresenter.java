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
      remainingDaysFormatted = PresenterUtils.formatRemainingDays(v.dueDate(), locale, i18n);
    }

    CampusResponse campus = SharedDataPresenter.createCampusResponse(v.campus(), locale, i18n);
    AuditInfoResponse auditInfo = SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

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
}
