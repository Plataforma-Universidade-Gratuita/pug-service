package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.presenter.dtos.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceInfoResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceStatusResponse;
import br.org.catolicasc.pug.project.presenter.dtos.AttendanceValidateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.QrValidationInfoResponse;
import br.org.catolicasc.pug.project.presenter.dtos.StudentSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.service.dtos.AttendanceCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.AttendanceValidateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping attendance requests and read-side projections.
 */
public final class AttendancePresenter {

  private AttendancePresenter() {}

  /**
   * Maps an incoming REST creation request into an application-layer creation command.
   *
   * @param req the validated attendance creation payload
   * @return the corresponding command, or {@code null} when the input payload is null
   */
  public static AttendanceCreateCommand toCommand(AttendanceCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new AttendanceCreateCommand(req.projectId(), req.formerStudentId(), req.duration());
  }

  /**
   * Maps an incoming REST validation request into an application-layer validation command.
   *
   * @param req the validated attendance validation payload
   * @return the corresponding command, or {@code null} when the input payload is null
   */
  public static AttendanceValidateCommand toCommand(AttendanceValidateRequest req) {
    if (req == null) {
      return null;
    }
    return new AttendanceValidateCommand(req.qrValidationHash(), req.status());
  }

  /**
   * Projects a read-side attendance view into the canonical single-record attendance response.
   *
   * @param view the internal read-model projection of the attendance
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated single-record response, or {@code null} when any required input is
   *     null
   */
  public static AttendanceResponse toResponse(AttendanceView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    return new AttendanceResponse(
        view.id(),
        view.projectId(),
        view.formerStudentId(),
        toStatusResponse(view, locale, i18n),
        toAttendanceInfoResponse(view, locale),
        toQrValidationInfoResponse(view));
  }

  /**
   * Projects a read-side attendance view into the paginated complex-search response payload.
   *
   * @param view the internal read-model projection of the attendance
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated complex-search response, or {@code null} when any required input is
   *     null
   */
  public static AttendanceComplexSearchResponse toComplexSearchResponse(
      AttendanceView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    return new AttendanceComplexSearchResponse(
        view.id(),
        new ProjectSimpleComplexSearchResponse(view.projectId(), view.projectName()),
        new StudentSimpleComplexSearchResponse(
            new AccountSimpleComplexSearchResponse(
                view.formerStudentId(), view.studentName(), view.studentEmail()),
            view.academicRegistration(),
            SharedDataPresenter.createCampusResponse(view.campus(), locale, i18n)),
        toStatusResponse(view, locale, i18n),
        toAttendanceInfoResponse(view, locale),
        view.validatedById() == null
            ? null
            : new AccountSimpleComplexSearchResponse(
                view.validatedById(), view.validatedByName(), view.validatedByEmail()),
        toQrValidationInfoResponse(view));
  }

  private static AttendanceInfoResponse toAttendanceInfoResponse(
      AttendanceView view, Locale locale) {
    if (view == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);

    return new AttendanceInfoResponse(
        view.validatedById(),
        view.validatedAt(),
        StringUtils.toStringFormatted(view.validatedAt(), locale),
        auditInfo);
  }

  private static QrValidationInfoResponse toQrValidationInfoResponse(AttendanceView view) {
    if (view == null) {
      return null;
    }
    return new QrValidationInfoResponse(view.duration(), view.qrValidationHash());
  }

  private static AttendanceStatusResponse toStatusResponse(
      AttendanceView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }
    return new AttendanceStatusResponse(
        view.status(), i18n.translation(view.status().getBundleKey(), locale));
  }
}
