package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/** Mapper for SchoolView to SchoolResponse. */
public final class SchoolPresenter {
  /** Private constructor to prevent instantiation. */
  private SchoolPresenter() {}

  /**
   * Maps a SchoolView to a SchoolResponse.
   *
   * @param v the SchoolView to map
   * @return the mapped SchoolResponse
   */
  public static SchoolResponse toResponse(SchoolView v, Locale locale) {
    if (v == null || locale == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new SchoolResponse(v.id(), v.name(), auditInfo);
  }
}
