package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.text.StringUtils;
import java.util.Locale;

public final class UserPresenter {
  private UserPresenter() {}

  public static UserResponse toResponse(UserView v, Locale locale, I18n i18n) {
    String label = i18n.translation(v.accountType().getBundleKey(), locale);
    String createdAtLabel = StringUtils.formatDateTime(v.createdAt(), locale);
    return new UserResponse(
        v.id(),
        v.cpf(),
        v.name(),
        v.email(),
        v.accountType(),
        label,
        v.createdAt(),
        createdAtLabel);
  }
}
