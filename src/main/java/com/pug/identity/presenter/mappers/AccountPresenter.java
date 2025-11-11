package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/** Mapper class for UserPresenter. */
public final class AccountPresenter {
  /** Private constructor to prevent instantiation. */
  private AccountPresenter() {}

  /**
   * Converts a UserView to a UserResponse.
   *
   * @param v the UserView
   * @param locale the locale for formatting
   * @param i18n the internationalization instance
   * @return the corresponding UserResponse
   */
  public static AccountResponse toResponse(AccountView v, Locale locale, I18n i18n) {
    String typeFormatted = i18n.translation(v.accountType().getBundleKey(), locale);
    String createdAtFormatted = StringUtils.toStringFormatted(v.createdAt(), locale);
    return new AccountResponse(
        v.id(),
        UserPresenter.toResponse(v.person(), locale),
        v.email(),
        v.accountType(),
        typeFormatted,
        v.createdAt(),
        createdAtFormatted);
  }
}
