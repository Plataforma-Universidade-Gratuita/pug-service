package com.pug.partner.presenter.mappers;

import com.pug.identity.presenter.mappers.AccountPresenter;
import com.pug.partner.infra.read.dtos.StaffView;
import com.pug.partner.presenter.dtos.StaffResponse;
import com.pug.shared.i18n.I18n;

import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal staff projections
 * to external API responses.
 * <p>
 * This presenter acts as a translation layer, converting deeply nested CQRS query views
 * ({@link StaffView}) into consolidated, client-ready representations ({@link StaffResponse}).
 */
public final class StaffPresenter {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private StaffPresenter() {
  }

  /**
   * Projects a read-only {@link StaffView} into a client-facing {@link StaffResponse}.
   * <p>
   * This mapping delegates the formatting of the underlying account and user data to
   * the Identity domain's {@link AccountPresenter}, and delegates the formatting of the
   * organizational details to the {@link EntityPresenter}.
   *
   * @param v      the internal read-model projection of the staff member
   * @param locale the locale extracted from the client's request headers
   * @param i18n   the internationalization service for resolving bundle keys
   * @return a fully populated {@link StaffResponse} ready for JSON serialization,
   * or {@code null} if any required input is null
   */
  public static StaffResponse toResponse(StaffView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }
    return new StaffResponse(
            AccountPresenter.toResponse(v.account(), locale, i18n),
            EntityPresenter.toResponse(v.entity(), locale));
  }
}