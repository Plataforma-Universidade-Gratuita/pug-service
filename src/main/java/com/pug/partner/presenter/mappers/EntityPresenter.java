package com.pug.partner.presenter.mappers;

import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityResponse;

/** Maps read-side EntityView to presenter EntityResponse. */
public final class EntityPresenter {
  /** Private constructor to prevent instantiation. */
  private EntityPresenter() {}

  /**
   * Maps EntityView to EntityResponse.
   *
   * @param v the EntityView
   * @return the EntityResponse
   */
  public static EntityResponse toResponse(EntityView v) {
    if (v == null) {
      return null;
    }
    return new EntityResponse(
        v.id(),
        v.cnpj(),
        formatted(v.cnpj()),
        v.name(),
        v.address(),
        CityPresenter.toResponse(v.city()));
  }

  /**
   * Returns the formatted CNPJ string in the pattern XX.XXX.XXX/XXXX-XX.
   *
   * @return the formatted CNPJ string
   */
  private static String formatted(String value) {
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
