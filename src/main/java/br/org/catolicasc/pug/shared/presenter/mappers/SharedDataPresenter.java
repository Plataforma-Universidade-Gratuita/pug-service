/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.presenter.mappers;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.i18n.I18n;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.CampusResponse;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * Stateless utility class responsible for transforming shared domain components into standardized
 * API Response DTOs.
 *
 * <p>This presenter handles common presentation logic used across multiple domains, such as:
 *
 * <ul>
 *   <li>Formatting temporal audit fields (createdAt / updatedAt) based on the user's locale.
 *   <li>Translating {@link Campi} enums into localized UI-ready response objects.
 * </ul>
 *
 * <p>Being a pure utility class, it cannot be instantiated.
 */
public final class SharedDataPresenter {

  private SharedDataPresenter() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  /**
   * Constructs an {@link AuditInfoResponse}, formatting the provided timestamps according to the
   * specified locale.
   *
   * @param createdAt the exact timestamp when the entity was created
   * @param updatedAt the exact timestamp when the entity was last updated
   * @param locale the client's resolved locale used to determine date formatting rules
   * @return a fully populated {@link AuditInfoResponse}, or {@code null} if any input parameter is
   *     null
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
   * Constructs a {@link CampusResponse} by leveraging the i18n service to translate the provided
   * {@link Campi} enum.
   *
   * @param campus the specific {@link Campi} enum to convert
   * @param locale the client's resolved locale used for the translation lookup
   * @param i18n the internationalization service component used to resolve the bundle key
   * @return a fully populated {@link CampusResponse}, or {@code null} if any input parameter is
   *     null
   */
  public static CampusResponse createCampusResponse(Campi campus, Locale locale, I18n i18n) {
    if (campus == null || locale == null || i18n == null) {
      return null;
    }
    String campusFormatted = i18n.translation(campus.getBundleKey(), locale);
    return new CampusResponse(campus, campusFormatted);
  }
}
