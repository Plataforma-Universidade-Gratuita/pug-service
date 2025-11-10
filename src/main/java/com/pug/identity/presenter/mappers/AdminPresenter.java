package com.pug.identity.presenter.mappers;

import com.pug.identity.presenter.dtos.AdminResponse;
import com.pug.identity.presenter.dtos.AdminView;
import com.pug.shared.i18n.I18n;
import java.util.Locale;

/** Mapper class for converting between AdminView and AdminResponse. */
public final class AdminPresenter {
  /** Private constructor to prevent instantiation. */
  private AdminPresenter() {}

  /**
   * Converts an AdminView into an AdminResponse.
   *
   * @param v the AdminView to convert from.
   * @param locale the current language.
   * @param i18n the translation system.
   * @return an API endpoint ready response.
   */
  public static AdminResponse toResponse(AdminView v, Locale locale, I18n i18n) {
    String label = i18n.translation(v.accountType().getBundleKey(), locale);
    return new AdminResponse(
        v.userId(),
        v.cpf(),
        v.name(),
        v.email(),
        v.accountType(),
        label,
        v.createdAt(),
        v.grantedAt());
  }
}
