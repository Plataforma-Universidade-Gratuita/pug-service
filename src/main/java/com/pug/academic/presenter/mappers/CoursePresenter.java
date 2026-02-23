package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/** Mapper class for converting CourseView to CourseResponse. */
public final class CoursePresenter {
  /** Private constructor to prevent instantiation. */
  private CoursePresenter() {}

  /**
   * Converts a CourseView object to a CourseResponse object.
   *
   * @param v the CourseView object to convert
   * @return the corresponding CourseResponse object
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
