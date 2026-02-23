package com.pug.shared.presenter.mappers;

import com.pug.shared.domain.enums.Campi;
import com.pug.shared.i18n.I18n;
import com.pug.shared.presenter.dtos.AuditInfoResponse;
import com.pug.shared.presenter.dtos.CampusResponse;
import com.pug.shared.utils.StringUtils;
import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * Utility class responsible for transforming shared domain data into standardized API Response
 * DTOs.
 *
 * <p>This presenter handles common mapping scenarios used across multiple domains, such as:
 *
 * <ul>
 *   <li>Formatting Audit timestamps (created/updated at) based on the account's locale.
 *   <li>Converting Campus enums into localized response objects.
 * </ul>
 *
 * <p>This class is stateless and cannot be instantiated.
 */
public final class SharedDataPresenter {

  /** Private constructor to prevent instantiation of the utility class. */
  private SharedDataPresenter() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Constructs an {@link AuditInfoResponse} with localized date formatting.
   *
   * <p><b>Note:</b> {@code updatedAt} is optional (nullable), but {@code createdAt} and {@code
   * locale} are mandatory.
   *
   * @param createdAt the timestamp when the entity was created (cannot be null).
   * @param updatedAt the timestamp when the entity was last updated (can be null).
   * @param locale the account's locale for date formatting (cannot be null).
   * @return an {@link AuditInfoResponse} containing raw timestamps and their string
   *     representations.
   */
  public static AuditInfoResponse createAuditInfoResponse(
      OffsetDateTime createdAt, OffsetDateTime updatedAt, Locale locale) {
    if (createdAt == null || updatedAt == null || locale == null) {
      return null;
    }

    String createdAtFormatted = StringUtils.toStringFormatted(createdAt, locale);
    String updatedAtFormatted = StringUtils.toStringFormatted(updatedAt, locale);

    return new AuditInfoResponse(createdAt, createdAtFormatted, updatedAt, updatedAtFormatted);
  }

  /**
   * Constructs a {@link CampusResponse} by translating the Campi enum.
   *
   * @param campus the specific {@link Campi} enum to convert (cannot be null).
   * @param locale the account's locale for translation (cannot be null).
   * @param i18n the internationalization service (cannot be null).
   * @return a {@link CampusResponse} containing the enum key and the localized display name.
   */
  public static CampusResponse createCampusResponse(Campi campus, Locale locale, I18n i18n) {
    if (campus == null || locale == null || i18n == null) {
      return null;
    }

    String campusFormatted = i18n.translation(campus.getBundleKey(), locale);

    return new CampusResponse(campus, campusFormatted);
  }
}
