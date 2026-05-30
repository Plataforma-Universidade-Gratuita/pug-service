package br.org.catolicasc.pug.academic.presenter.mappers;

import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseComplexSearchResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseCreateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseResponse;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseUpdateRequest;
import br.org.catolicasc.pug.academic.presenter.dtos.courses.CourseWithAuditInfoComplexSearchResponse;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseUpdateCommand;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal academic course projections to external
 * API responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * CourseView}) into client-ready representations ({@link CourseResponse}). It also delegates the
 * mapping of the nested academic area-of-expertise data to the {@link AreaOfExpertisePresenter}.
 */
public final class CoursePresenter {
  /** Private constructor to prevent instantiation. */
  private CoursePresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer course creation command.
   *
   * <p>This helper is responsible only for the course portion of the payload. It extracts the raw
   * name and areaOfExpertise identifier and encapsulates them into a {@link CourseCreateCommand},
   * leaving domain validation and orchestration to the application service layer.
   *
   * @param req the validated {@link CourseCreateRequest} payload
   * @return the corresponding {@link CourseCreateCommand}, or {@code null} if the request is null
   */
  public static CourseCreateCommand toCommand(CourseCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new CourseCreateCommand(req.name(), req.areaOfExpertiseId());
  }

  /**
   * Maps an incoming REST update request into an application layer course update command.
   *
   * <p>This helper is responsible only for the course portion of the update payload. Because
   * updates can be partial, it directly propagates the potentially {@code null} fields to the
   * {@link CourseUpdateCommand}, allowing the service layer to interpret omitted values as "no
   * change".
   *
   * @param req the validated {@link CourseUpdateRequest} payload containing the modified data
   * @return the corresponding {@link CourseUpdateCommand}, or {@code null} if the request is null
   */
  public static CourseUpdateCommand toCommand(CourseUpdateRequest req) {
    if (req == null) {
      return null;
    }
    return new CourseUpdateCommand(req.name(), req.areaOfExpertiseId());
  }

  /**
   * Projects a read-only {@link CourseView} into a client-facing {@link CourseResponse}.
   *
   * @param v the internal read-model projection of the course
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link CourseResponse} ready for JSON serialization, or {@code null}
   *     if the input view is null
   */
  public static CourseResponse toResponse(CourseView v, Locale locale) {
    if (v == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new CourseResponse(
        v.id(),
        v.name(),
        AreaOfExpertisePresenter.toResponse(v.areaOfExpertise(), locale),
        auditInfo);
  }

  /**
   * Projects a read-only {@link CourseView} into the lightweight course response used by nested
   * complex-search payloads.
   *
   * @param view the internal read-model projection of the course
   * @return the lightweight nested complex-search response, or {@code null} if the input is null
   */
  public static CourseComplexSearchResponse toComplexSearchResponse(CourseView view) {
    if (view == null) {
      return null;
    }
    return new CourseComplexSearchResponse(
        view.id(),
        view.name(),
        AreaOfExpertisePresenter.toComplexSearchResponse(view.areaOfExpertise()));
  }

  /**
   * Projects a read-only {@link CourseView} into the public response used by the course
   * complex-search endpoint.
   *
   * @param view the internal read-model projection of the course
   * @param locale the locale extracted from the client's request headers
   * @return the paginated course-search response payload, or {@code null} if the input is invalid
   */
  public static CourseWithAuditInfoComplexSearchResponse toWithAuditInfoComplexSearchResponse(
      CourseView view, Locale locale) {
    if (view == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(view.createdAt(), view.updatedAt(), locale);

    return new CourseWithAuditInfoComplexSearchResponse(
        view.id(),
        view.name(),
        AreaOfExpertisePresenter.toComplexSearchResponse(view.areaOfExpertise()),
        auditInfo);
  }
}
