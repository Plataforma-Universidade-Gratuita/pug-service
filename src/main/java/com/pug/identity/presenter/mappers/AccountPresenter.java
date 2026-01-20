package com.pug.identity.presenter.mappers;

import com.pug.identity.domain.enums.IdentityErrorCodes;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.identity.presenter.dtos.UserResponse;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.i18n.I18n;
import com.pug.shared.utils.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * Mapper class for converting AccountView to AccountResponse.
 */
public final class AccountPresenter {
  /**
   * Private constructor to prevent instantiation.
   */
  private AccountPresenter() {
  }

  /**
   * Converts an AccountView to an AccountResponse.
   *
   * @param v      the AccountView
   * @param locale the locale for formatting
   * @param i18n   the internationalization instance
   * @return the corresponding AccountResponse
   * @throws AppValidationException    if internal data (e.g., user's CPF) is invalid during conversion to UserResponse.
   *                                   This would indicate corrupted data in the read model.
   * @throws ResourceNotFoundException if user data within AccountView is null, preventing UserResponse creation.
   */
  public static AccountResponse toResponse(AccountView v, Locale locale, I18n i18n) {
    if (v == null) {
      return null;
    }

    if (v.user() == null) {
      throw new ResourceNotFoundException(
              IdentityErrorCodes.USER_NOT_FOUND, Map.of("accountId", v.id().toString(), "detail", "Associated user data is missing."));
    }

    UserResponse userResponse = UserPresenter.toResponse(v.user(), locale);
    String typeFormatted = i18n.translation(v.accountType().getBundleKey(), locale);
    String createdAtFormatted = StringUtils.toStringFormatted(v.createdAt(), locale);

    return new AccountResponse(
            v.id(),
            userResponse,
            v.email(),
            v.accountType(),
            typeFormatted,
            v.createdAt(),
            createdAtFormatted);
  }
}