package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/** Mapper class for AdminPresenter. */
public final class AdminPresenter {
  /** Private constructor to prevent instantiation. */
  private AdminPresenter() {}

  /**
   * Converts an AdminView to an AdminResponse.
   *
   * @param a the AdminView
   * @param locale the locale for formatting
   * @param i18n the internationalization instance
   * @return the corresponding AdminResponse
   */
  public static AdminResponse toResponse(AdminView a, Locale locale, I18n i18n) {
    String grantedAtLabel = StringUtils.toStringFormatted(a.grantedAt(), locale);
    return new AdminResponse(
        UserPresenter.toResponse(a.userView(), locale, i18n), a.grantedAt(), grantedAtLabel);
  }
}
