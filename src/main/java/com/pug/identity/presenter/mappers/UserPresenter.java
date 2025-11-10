package com.pug.identity.presenter.mappers;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.text.StringUtils;
import java.util.Locale;

/** Mapper class for UserPresenter. */
public final class UserPresenter {
  /** Private constructor to prevent instantiation. */
  private UserPresenter() {}

  /**
   * Converts a UserView to a UserResponse.
   *
   * @param v the UserView
   * @param locale the locale for formatting
   * @param i18n the internationalization instance
   * @return the corresponding UserResponse
   */
  public static UserResponse toResponse(UserView v, Locale locale, I18n i18n) {
    String label = i18n.translation(v.accountType().getBundleKey(), locale);
    String createdAtLabel = StringUtils.formatDateTime(v.createdAt(), locale);
    String formattedCpf = new Cpf(v.cpf()).formatted();
    return new UserResponse(
        v.id(),
        formattedCpf,
        v.name(),
        v.email(),
        v.accountType(),
        label,
        v.createdAt(),
        createdAtLabel);
  }
}
