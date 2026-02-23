package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.CampusResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import com.pug.shared.utils.StringUtils;

import java.util.Locale;

/**
 * Mapper class for converting AdminView to AdminResponse.
 */
public final class AdminPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private AdminPresenter() {
  }

  /**
   * Converts an AdminView to an AdminResponse.
   *
   * @param a      the AdminView.
   * @param locale the locale for formatting.
   * @param i18n   the internationalization instance.
   * @return the corresponding AdminResponse.
   */
  public static AdminResponse toResponse(AdminView a, Locale locale, I18n i18n) {
    if (a == null || locale == null || i18n == null) {
      return null;
    }

    AccountResponse accountResponse = AccountPresenter.toResponse(a.accountView(), locale, i18n);
    CampusResponse campus = SharedDataPresenter.createCampusResponse(a.campus(), locale, i18n);
    String grantedAtFormatted = StringUtils.toStringFormatted(a.grantedAt(), locale);

    return new AdminResponse(accountResponse, campus, a.grantedAt(), grantedAtFormatted);
  }
}
