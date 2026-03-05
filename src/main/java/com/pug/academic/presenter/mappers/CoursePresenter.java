package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseResponse;
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
