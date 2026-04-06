package com.pug.project.presenter.mappers;

import com.pug.academic.presenter.mappers.SchoolPresenter;
import com.pug.project.infra.read.dtos.ProjectView;
import com.pug.project.infra.read.dtos.SchoolProjectView;
import com.pug.project.presenter.dtos.ProjectCreateRequest;
import com.pug.project.presenter.dtos.ProjectResponse;
import com.pug.project.presenter.dtos.ProjectUpdateRequest;
import com.pug.project.presenter.dtos.ProjectsBySchoolResponse;
import com.pug.project.service.dtos.ProjectCreateCommand;
import com.pug.project.service.dtos.ProjectUpdateCommand;
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
   * Maps an incoming REST creation request into an application layer project creation command.
   *
   * <p>This helper is responsible only for the project portion of the payload. It extracts the raw
   * data from the {@link ProjectCreateRequest} and encapsulates it into a {@link
   * ProjectCreateCommand}, leaving domain validation and orchestration to the application service
   * layer.
   *
   * @param req the validated {@link ProjectCreateRequest} payload
   * @return the corresponding {@link ProjectCreateCommand}, or {@code null} if the request is null
   */
  public static ProjectCreateCommand toCommand(ProjectCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new ProjectCreateCommand(
        req.name(),
        req.entityId(),
        req.description(),
        req.maxParticipants(),
        req.offeredHours(),
        req.schoolId());
  }

  /**
   * Maps an incoming REST update request into an application layer project update command.
   *
   * <p>This helper is responsible only for the project portion of the update payload. Because
   * updates can be partial, it directly propagates the potentially {@code null} fields to the
   * {@link ProjectUpdateCommand}, allowing the service layer to interpret omitted values as "no
   * change".
   *
   * @param req the validated {@link ProjectUpdateRequest} payload
   * @return the corresponding {@link ProjectUpdateCommand}, or {@code null} if the request is null
   */
  public static ProjectUpdateCommand toCommand(ProjectUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new ProjectUpdateCommand(
        req.name(), req.description(), req.maxParticipants(), req.offeredHours(), req.schoolId());
  }

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
