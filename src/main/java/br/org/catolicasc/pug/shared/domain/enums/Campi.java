package br.org.catolicasc.pug.shared.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing the university campuses and their corresponding localization data.
 *
 * <p>This enum identifies the available academic campuses (Jaraguá do Sul and Joinville) within the
 * platform's business domain. It maps each campus to its specific internationalization (i18n) key,
 * ensuring consistent and localized UI representation across the application.
 */
@Getter
@AllArgsConstructor
public enum Campi implements GenericCodes {

  /**
   * Represents the Jaraguá do Sul campus.
   *
   * <p>Mapped to the {@code academic.campus.jaragua} localization key.
   */
  JARAGUA_DO_SUL("academic.campus.jaragua"),

  /**
   * Represents the Joinville campus.
   *
   * <p>Mapped to the {@code academic.campus.joinville} localization key.
   */
  JOINVILLE("academic.campus.joinville");

  private final String bundleKey;
}
