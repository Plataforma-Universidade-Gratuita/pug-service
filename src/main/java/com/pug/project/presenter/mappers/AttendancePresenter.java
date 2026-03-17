package com.pug.project.presenter.mappers;

import com.pug.academic.presenter.mappers.StudentPresenter;
import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.project.infra.read.dtos.AttendanceView;
import com.pug.project.presenter.dtos.AttendanceResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal attendance projections to external API
 * responses.
 */
public final class AttendancePresenter {
  private AttendancePresenter() {}

  public static AttendanceResponse toResponse(AttendanceView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String statusFormatted = i18n.translation(v.status().getBundleKey(), locale);
    String validatedAtFormatted = StringUtils.toStringFormatted(v.validatedAt(), locale);

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new AttendanceResponse(
        v.id(),
        ProjectPresenter.toResponse(v.project(), locale, i18n),
        StudentPresenter.toResponse(v.student(), locale, i18n),
        v.duration(),
        v.latitude(),
        v.longitude(),
        v.qrValidationHash(),
        v.status(),
        statusFormatted,
        AccountPresenter.toResponse(v.validatedBy(), locale, i18n),
        v.validatedAt(),
        validatedAtFormatted,
        auditInfo);
  }
}
