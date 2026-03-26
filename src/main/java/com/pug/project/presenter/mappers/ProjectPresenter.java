package com.pug.project.presenter.mappers;

import com.pug.academic.presenter.mappers.SchoolPresenter;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.project.presenter.dtos.ProjectResponse;
import com.pug.project.presenter.dtos.ProjectsBySchoolResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal project projections to external API
 * responses.
 */
public final class ProjectPresenter {
  private ProjectPresenter() {}

  /**
   * Projects a read-only {@link ProjectView} into a client-facing {@link ProjectResponse}.
   *
   * @param v the internal read-model projection of the project
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service
   * @return a fully populated {@link ProjectResponse} ready for JSON serialization
   */
  public static ProjectResponse toResponse(ProjectView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String statusFormatted = i18n.translation(v.status().getBundleKey(), locale);
    String closedAtFormatted = StringUtils.toStringFormatted(v.closedAt(), locale);

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new ProjectResponse(
        v.id(),
        v.name(),
        v.entityId(),
        v.description(),
        v.creatorId(),
        v.maxParticipants(),
        v.offeredHours(),
        v.status(),
        statusFormatted,
        v.closedAt(),
        closedAtFormatted,
        auditInfo);
  }

  /**
   * Projects a read-only {@link SchoolProjectView} into a client-facing {@link
   * ProjectsBySchoolResponse}.
   *
   * @param v the internal read-model projection containing school and projects
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service
   * @return a fully populated {@link ProjectsBySchoolResponse}
   */
  public static ProjectsBySchoolResponse toResponse(SchoolProjectView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    return new ProjectsBySchoolResponse(
        SchoolPresenter.toResponse(v.school(), locale),
        v.projects().stream().map(p -> toResponse(p, locale, i18n)).toList());
  }
}
