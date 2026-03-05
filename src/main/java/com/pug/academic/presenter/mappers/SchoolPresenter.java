package com.pug.academic.presenter.mappers;

import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.presenter.dtos.SchoolResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;

import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal academic school projections
 * to external API responses.
 * <p>
 * This presenter acts as a translation layer, converting raw CQRS query views ({@link SchoolView})
 * into client-ready representations ({@link SchoolResponse}).
 */
public final class SchoolPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private SchoolPresenter() {
  }

  /**
   * Projects a read-only {@link SchoolView} into a client-facing {@link SchoolResponse}.
   *
   * @param v      the internal read-model projection of the school
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link SchoolResponse} ready for JSON serialization,
   * or {@code null} if the input view is null
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