package com.pug.partner.presenter.mappers;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal partner entity projections to external
 * API responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * EntityView}) into client-ready representations ({@link EntityResponse}). It is responsible for
 * injecting presentation-specific formatting, such as standardizing the Brazilian CNPJ string.
 */
public final class EntityPresenter {

  /** Private constructor to prevent instantiation of utility class. */
  private EntityPresenter() {}

  /**
   * Projects a read-only {@link EntityView} into a client-facing {@link EntityResponse}.
   *
   * <p>This mapping cascades down to format the nested {@link CityResponse} and resolves localized
   * formatting for audit timestamps and corporate identification numbers.
   *
   * @param v the internal read-model projection of the partner entity
   * @param locale the locale extracted from the client's request headers
   * @return a fully populated {@link EntityResponse} ready for JSON serialization, or {@code null}
   *     if the input view is null
   */
  public static EntityResponse toResponse(EntityView v, Locale locale) {
    if (v == null) {
      return null;
    }

    String formattedCnpj = toFormattedString(v.cnpj());
    CityResponse cityResponse = CityPresenter.toResponse(v.city());
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new EntityResponse(
        v.id(), v.cnpj(), formattedCnpj, v.name(), v.address(), cityResponse, auditInfo);
  }

  /**
   * Computes the formatted string representation of a Brazilian CNPJ.
   *
   * <p>Transforms a raw 14-digit numeric string (e.g., "12345678000199") into the standard
   * punctuated format (e.g., "12.345.678/0001-99"). If the input is null or not exactly 14
   * characters, the raw value is returned safely to prevent exceptions.
   *
   * @param value the raw numeric CNPJ string
   * @return the punctuated CNPJ string, or the raw input if formatting is not possible
   */
  private static String toFormattedString(String value) {
    if (value == null || value.length() != 14) {
      return value;
    }
    return value.substring(0, 2)
        + "."
        + value.substring(2, 5)
        + "."
        + value.substring(5, 8)
        + "/"
        + value.substring(8, 12)
        + "-"
        + value.substring(12, 14);
  }
}
