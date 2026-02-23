package com.pug.partner.presenter.mappers;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;

import java.util.Locale;

/**
 * Maps read-side EntityView to presenter EntityResponse.
 */
public final class EntityPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private EntityPresenter() {
  }

  /**
   * Maps EntityView to EntityResponse.
   *
   * @param v the EntityView
   * @return the EntityResponse
   */
  public static EntityResponse toResponse(EntityView v, Locale locale) {
    if (v == null) {
      return null;
    }

    String formattedCnpj = toFormattedString(v.cnpj());
    CityResponse cityResponse = CityPresenter.toResponse(v.city());
    AuditInfoResponse auditInfo = SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new EntityResponse(v.id(), v.cnpj(), formattedCnpj, v.name(), v.address(), cityResponse, auditInfo);
  }

  /**
   * Returns the formatted string representation of the CNPJ (e.g., "XX.XXX.XXX/XXXX-XX"). Returns
   * raw value if length is invalid.
   *
   * @return the formatted CNPJ as a String.
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
