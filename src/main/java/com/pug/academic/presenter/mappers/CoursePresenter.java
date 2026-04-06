package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseCreateRequest;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.academic.presenter.dtos.CourseUpdateRequest;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal academic course projections to external
 * API responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * CourseView}) into client-ready representations ({@link CourseResponse}). It also delegates the
 * mapping of the nested academic school data to the {@link SchoolPresenter}.
 */
public final class CoursePresenter {
  /** Private constructor to prevent instantiation. */
  private CoursePresenter() {}

  /**
   * Maps an incoming REST creation request into an application layer course creation command.
   *
   * <p>This helper is responsible only for the course portion of the payload. It extracts the raw
   * name and school identifier and encapsulates them into a {@link CourseCreateCommand}, leaving
   * domain validation and orchestration to the application service layer.
   *
   * @param req the validated {@link CourseCreateRequest} payload
   * @return the corresponding {@link CourseCreateCommand}, or {@code null} if the request is null
   */
  public static CourseCreateCommand toCommand(CourseCreateRequest req) {
    if (req == null) {
      return null;
    }
    return new CourseCreateCommand(req.name(), req.schoolId());
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
    return new CourseUpdateCommand(req.name(), req.schoolId());
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
        v.id(), v.name(), SchoolPresenter.toResponse(v.school(), locale), auditInfo);
  }
}
