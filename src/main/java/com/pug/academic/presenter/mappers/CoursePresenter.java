package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.academic.presenter.dtos.CourseResponse;

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
  public static CourseResponse toResponse(CourseView v) {
    return new CourseResponse(v.id(), v.name(), SchoolPresenter.toResponse(v.school()));
  }
}
