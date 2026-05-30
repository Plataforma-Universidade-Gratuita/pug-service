package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentInfoResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentStatusResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectSimpleComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;
import java.util.UUID;

public final class EnrollmentPresenter {

  private EnrollmentPresenter() {}

  public static EnrollmentCreateCommand toCommand(UUID projectId, UUID formerStudentId) {
    return new EnrollmentCreateCommand(projectId, formerStudentId);
  }

  public static EnrollmentResponse toResponse(EnrollmentView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }
    return new EnrollmentResponse(
        view.projectId(),
        view.formerStudentId(),
        createStatusResponse(view, locale, i18n),
        createInfoResponse(view, locale));
  }

  public static EnrollmentComplexSearchResponse toComplexSearchResponse(
      EnrollmentView view, Locale locale, I18n i18n) {
    if (view == null || locale == null || i18n == null) {
      return null;
    }

    CampusResponse campus = SharedDataPresenter.createCampusResponse(view.campus(), locale, i18n);

    return new EnrollmentComplexSearchResponse(
        new ProjectSimpleComplexSearchResponse(view.projectId(), view.projectName()),
        new FormerStudentSimpleComplexSearchResponse(
            new AccountSimpleComplexSearchResponse(
                view.formerStudentId(), view.formerStudentName(), view.formerStudentEmail()),
            view.academicRegistration(),
            campus),
        createStatusResponse(view, locale, i18n),
        createInfoResponse(view, locale));
  }

  private static EnrollmentStatusResponse createStatusResponse(
      EnrollmentView view, Locale locale, I18n i18n) {
    return new EnrollmentStatusResponse(
        view.status(), i18n.translation(view.status().getBundleKey(), locale));
  }

  private static EnrollmentInfoResponse createInfoResponse(EnrollmentView view, Locale locale) {
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);

    return new EnrollmentInfoResponse(
        view.acceptedAt(),
        StringUtils.toStringFormatted(view.acceptedAt(), locale),
        view.closingStatusAt(),
        StringUtils.toStringFormatted(view.closingStatusAt(), locale),
        auditInfo);
  }
}
