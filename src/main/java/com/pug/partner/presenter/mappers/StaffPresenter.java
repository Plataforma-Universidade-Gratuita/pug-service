package com.pug.partner.presenter.mappers;

import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.shared.i18n.I18n;
import java.util.Locale;

/** Maps read-side StaffView to presenter StaffResponse. */
public final class StaffPresenter {
  /** Private constructor to prevent instantiation. */
  private StaffPresenter() {}

  /**
   * Maps StaffView to StaffResponse.
   *
   * @param v the StaffView
   * @param locale the locale for localization
   * @param i18n the internationalization utility
   * @return the StaffResponse
   */
  public static StaffResponse toResponse(StaffView v, Locale locale, I18n i18n) {
    if (v == null) {
      return null;
    }
    return new StaffResponse(
        AccountPresenter.toResponse(v.account(), locale, i18n),
        EntityPresenter.toResponse(v.entity()));
  }
}
