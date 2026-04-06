package com.pug.identity.presenter.mappers;

import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.presenter.dtos.AccountResponse;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.mappers.SharedDataPresenter;
import java.util.Locale;

/**
 * Stateless utility class responsible for mapping internal identity projections to external API
 * responses.
 *
 * <p>This presenter acts as a translation layer, converting raw CQRS query views ({@link
 * AccountView}) into client-ready representations ({@link AccountResponse}). It is responsible for
 * injecting presentation-specific formatting, such as resolving the localized string for the
 * account type enum based on the client's {@link Locale}.
 */
public final class AccountPresenter {

  /** Private constructor to prevent instantiation of utility class. */
  private AccountPresenter() {}

  /**
   * Projects a read-only {@link AccountView} into a client-facing {@link AccountResponse}.
   *
   * <p>This mapping produces a flattened response: instead of nesting the full user payload, it
   * exposes only the {@code userId}, allowing clients to retrieve detailed user information on
   * demand via dedicated user endpoints. It also resolves the localized label for the account type
   * and formats the audit timestamps according to the provided {@link Locale}.
   *
   * @param v the internal read-model projection of the account
   * @param locale the locale extracted from the client's request headers
   * @param i18n the internationalization service for resolving bundle keys
   * @return a fully populated {@link AccountResponse} ready for JSON serialization, or {@code null}
   *     if any required input is null
   */
  public static AccountResponse toResponse(AccountView v, Locale locale, I18n i18n) {
    if (v == null || locale == null || i18n == null) {
      return null;
    }

    String typeFormatted = i18n.translation(v.accountType().getBundleKey(), locale);
    AuditInfoResponse auditInfo =
        SharedDataPresenter.createAuditInfoResponse(v.createdAt(), v.updatedAt(), locale);

    return new AccountResponse(
        v.id(), v.userId(), v.email(), v.accountType(), typeFormatted, auditInfo, v.active());
  }
}
