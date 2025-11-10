package com.pug.geo.presenter.mappers;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.geo.presenter.dtos.CityResponse;

/** Maps read-side CityView to presenter CityResponse. */
public final class CityPresenter {
  private CityPresenter() {}

  public static CityResponse toResponse(CityView v) {
    if (v == null) return null;
    return new CityResponse(v.id(), v.name(), v.ibgeCode());
  }
}
