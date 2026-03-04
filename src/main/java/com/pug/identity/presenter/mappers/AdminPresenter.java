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
 * Stateless utility class responsible for mapping internal administrator projections
 * to external API responses.
 * <p>
 * This presenter acts as a translation layer, converting deeply nested CQRS query views
 * ({@link AdminView}) into consolidated, client-ready representations ({@link AdminResponse}).
 */
public final class AdminPresenter {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private AdminPresenter() {
  }

  /**
   * Projects a read-only {@link AdminView} into a client-facing {@link AdminResponse}.
   * <p>
   * This mapping delegates the formatting of the underlying account and user data to
   * the {@link AccountPresenter}, while directly formatting the admin-specific fields
   * (such as the localized date of the privilege grant and the resolved campus).
   *
   * @param a      the internal read-model projection of the administrator
   * @param locale the locale extracted from the client's request headers
   * @param i18n   the internationalization service for resolving bundle keys
   * @return a fully populated {@link AdminResponse} ready for JSON serialization,
   * or {@code null} if any required input is null
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