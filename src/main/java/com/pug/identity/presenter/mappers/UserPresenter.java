package com.pug.identity.presenter.mappers;

import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/** Mapper class for converting UserView to UserResponse. */
public final class UserPresenter {
  /** Private constructor to prevent instantiation. */
  private UserPresenter() {}

  /**
   * Converts a UserView to a UserResponse.
   *
   * @param v the UserView to convert
   * @param locale the locale for localization
   * @return the converted UserResponse
   * @throws AppValidationException if the CPF in the UserView is not valid when formatting. This
   *     might indicate corrupted data in the read model.
   */
  public static UserResponse toResponse(UserView v, Locale locale) {
    if (v == null) {
      return null;
    }
    String createdAtFormatted = StringUtils.toStringFormatted(v.createdAt(), locale);

    String formattedCpf;
    formattedCpf = new Cpf(v.cpf()).toFormattedString();

    return new UserResponse(
        v.id(), v.cpf(), formattedCpf, v.name(), v.createdAt(), createdAtFormatted);
  }
}
