package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.UserView;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
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
   */
  public static UserResponse toResponse(UserView v, Locale locale) {
    if (v == null) {
      return null;
    }

    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new UserResponse(v.id(), v.cpf(), cpfFormatted(v.cpf()), v.name(), auditInfo);
  }

  /**
   * Returns the formatted string representation of the CPF (e.g., "123.456.789-00"). Returns the
   * raw value if the CPF does not have the correct length (e.g., invalid state).
   *
   * @return the formatted CPF as a String.
   */
  private static String cpfFormatted(String cpf) {
    if (cpf == null || cpf.length() != 11) {
      return cpf;
    }
    return cpf.substring(0, 3)
        + "."
        + cpf.substring(3, 6)
        + "."
        + cpf.substring(6, 9)
        + "-"
        + cpf.substring(9, 11);
  }
}
