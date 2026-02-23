package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.StudentViewWithCompletedHours;
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
  public static StudentResponse toResponse(StudentViewWithCompletedHours v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }
    var vDetails = v.details();

    String startDateFormatted = StringUtils.toStringFormatted(vDetails.startDate(), locale);
    String dueDateFormatted = StringUtils.toStringFormatted(vDetails.dueDate(), locale);

    BigDecimal missingHours = BigDecimal.ZERO;
    if (vDetails.requiredHours() != null && v.completedHours() != null) {
      missingHours = vDetails.requiredHours().subtract(v.completedHours());
    }

    long remainingDays = 0;
    String remainingDaysFormatted = "";
    if (vDetails.dueDate() != null) {
      remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), vDetails.dueDate());
      remainingDaysFormatted = PresenterUtils.formatRemainingDays(vDetails.dueDate(), locale, i18n);
    }

    CampusResponse campus = SharedDataPresenter.createCampusResponse(vDetails.campus(), locale, i18n);
    AuditInfoResponse auditInfo = SharedDataPresenter.createAuditInfoResponse(vDetails.createdAt(), vDetails.updatedAt(), locale);

    return new StudentResponse(
            AccountPresenter.toResponse(vDetails.account(), locale, i18n),
            vDetails.academicRegistration(),
            campus,
            CoursePresenter.toResponse(vDetails.course(), locale),
            vDetails.requiredHours(),
            BigDecimal.ZERO,
            missingHours,
            vDetails.startDate(),
            startDateFormatted,
            vDetails.dueDate(),
            dueDateFormatted,
            remainingDays,
            remainingDaysFormatted,
            auditInfo);
  }
}
