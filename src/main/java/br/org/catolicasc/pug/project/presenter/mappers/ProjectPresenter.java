package br.org.catolicasc.pug.project.presenter.mappers;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectComplexSearchResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectCreateRequest;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectInfoResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectStatusResponse;
import br.org.catolicasc.pug.project.presenter.dtos.ProjectUpdateRequest;
import br.org.catolicasc.pug.project.service.dtos.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.ProjectUpdateCommand;
import br.org.catolicasc.pug.partner.presenter.dtos.EntitySimpleComplexSearchResponse;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import br.org.catolicasc.pug.shared.utils.StringUtils;
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
        req.name(), req.entityId(), req.description(), req.maxParticipants(), req.offeredHours());
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
        req.name(), req.description(), req.maxParticipants(), req.offeredHours());
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
    return new ProjectResponse(
        v.id(),
        v.name(),
        toEntityResponse(v),
        v.description(),
        toProjectInfoResponse(v, locale),
        new ProjectStatusResponse(v.status(), statusFormatted));
  }

  /**
   * Projects a read-only {@link ProjectView} into a client-facing complex-search response.
   *
   * @param v the internal read-model projection of the project
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service
   * @return a fully populated {@link ProjectComplexSearchResponse} ready for JSON serialization
   */
  public static ProjectComplexSearchResponse toComplexSearchResponse(
      ProjectView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    return new ProjectComplexSearchResponse(
        v.id(),
        v.name(),
        toEntityResponse(v),
        v.description(),
        toProjectInfoResponse(v, locale),
        new ProjectStatusResponse(v.status(), i18n.translation(v.status().getBundleKey(), locale)));
  }

  private static EntitySimpleComplexSearchResponse toEntityResponse(ProjectView v) {
    if (v == null) {
      return null;
    }
    return new EntitySimpleComplexSearchResponse(v.entityId(), v.entityName());
  }

  private static ProjectInfoResponse toProjectInfoResponse(ProjectView v, Locale locale) {
    if (v == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new ProjectInfoResponse(
        v.creatorId(),
        v.maxParticipants(),
        v.offeredHours(),
        v.completedHours(),
        v.closedAt(),
        StringUtils.toStringFormatted(v.closedAt(), locale),
        auditInfo);
  }
}
