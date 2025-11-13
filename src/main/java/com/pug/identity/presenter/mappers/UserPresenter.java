package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.utils.StringUtils;
import java.util.Locale;

/** Mapper class for converting PersonView to PersonResponse. */
public final class UserPresenter {
  /** Private constructor to prevent instantiation. */
  private UserPresenter() {}

  /**
   * Converts a PersonView to a PersonResponse.
   *
   * @param v the PersonView to convert
   * @param locale the locale for localization
   * @return the converted PersonResponse
   */
  public static UserResponse toResponse(UserView v, Locale locale) {
    String createdAtFormatted = StringUtils.toStringFormatted(v.createdAt(), locale);
    return new UserResponse(
        v.id(), v.cpf(), formatted(v.cpf()), v.name(), v.createdAt(), createdAtFormatted);
  }

  /**
   * Returns the formatted representation of the CPF (xxx.xxx.xxx-xx).
   *
   * @return the formatted CPF as a String
   */
  private static String formatted(String v) {
    return v.substring(0, 3)
        + "."
        + v.substring(3, 6)
        + "."
        + v.substring(6, 9)
        + "-"
        + v.substring(9);
  }
}
