/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.academic.presenter.dtos.formerstudents.FormerStudentSimpleComplexSearchResponse;
import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentInfoResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentResponse;
import br.org.catolicasc.pug.project.presenter.dtos.enrollments.EnrollmentStatusResponse;
import br.org.catolicasc.pug.project.presenter.dtos.projects.ProjectSimpleComplexSearchResponse;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.util.Locale;
import java.util.UUID;

/** Presenter mapper responsible for enrollment request, response, and search projections. */
public final class EnrollmentPresenter {

  private EnrollmentPresenter() {}

  /**
   * Builds the enrollment-creation command from route parameters.
   *
   * @param projectId the target project identifier
   * @param formerStudentId the optional target former-student identifier
   * @return the mapped service-layer command
   */
  public static EnrollmentCreateCommand toCommand(UUID projectId, UUID formerStudentId) {
    return new EnrollmentCreateCommand(projectId, formerStudentId);
  }

  /**
   * Maps a standard enrollment read projection to the canonical API response.
   *
   * @param view the read-side projection
   * @param locale the locale used to format timestamps
   * @param i18n the translation helper used for status localization
   * @return the mapped response, or {@code null} when any required input is {@code null}
   */
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

  /**
   * Maps an enrollment read projection to the complex-search response shape.
   *
   * @param view the read-side projection
   * @param locale the locale used to format timestamps and nested data
   * @param i18n the translation helper used for status and campus localization
   * @return the mapped response item, or {@code null} when any required input is {@code null}
   */
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
