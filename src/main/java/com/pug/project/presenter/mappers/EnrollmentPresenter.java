package com.pug.project.presenter.mappers;

import com.pug.academic.presenter.mappers.StudentPresenter;
import com.pug.project.infra.read.dtos.EnrollmentView;
import com.pug.project.presenter.dtos.EnrollmentResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal enrollment projections to external API
 * responses.
 */
public final class EnrollmentPresenter {
  private EnrollmentPresenter() {}

  public static EnrollmentResponse toResponse(EnrollmentView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String statusFormatted = i18n.translation(v.status().getBundleKey(), locale);
    String acceptedAtFormatted = StringUtils.toStringFormatted(v.acceptedAt(), locale);
    String closingStatusAtFormatted = StringUtils.toStringFormatted(v.closingStatusAt(), locale);

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new EnrollmentResponse(
        ProjectPresenter.toResponse(v.project(), locale, i18n),
        StudentPresenter.toResponse(v.student(), locale, i18n),
        v.status(),
        statusFormatted,
        v.acceptedAt(),
        acceptedAtFormatted,
        v.closingStatusAt(),
        closingStatusAtFormatted,
        auditInfo);
  }
}
