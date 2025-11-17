package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.presenter.dtos.SchoolResponse;

/**
 * Mapper for SchoolView to SchoolResponse.
 */
public final class SchoolPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private SchoolPresenter() {
  }

  /**
   * Maps a SchoolView to a SchoolResponse.
   *
   * @param v the SchoolView to map
   * @return the mapped SchoolResponse
   */
  public static SchoolResponse toResponse(SchoolView v) {
    return new SchoolResponse(v.id(), v.name());
  }
}
