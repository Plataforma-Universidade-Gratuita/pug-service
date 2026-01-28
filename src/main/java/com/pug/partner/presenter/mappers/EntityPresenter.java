package com.pug.partner.presenter.mappers;

import com.pug.geo.presenter.dtos.CityResponse;
import com.pug.geo.presenter.mappers.CityPresenter;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.presenter.dtos.EntityResponse;
import com.pug.shared.exceptions.AppValidationException;

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

    String formattedCnpj;
    formattedCnpj = Cnpj.factory(v.cnpj()).toFormattedString();

    CityResponse cityResponse = CityPresenter.toResponse(v.city());

    return new EntityResponse(v.id(), v.cnpj(), formattedCnpj, v.name(), v.address(), cityResponse);
  }
}
