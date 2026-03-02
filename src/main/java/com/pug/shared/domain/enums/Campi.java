package com.pug.shared.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enumeration representing the university campuses and their corresponding geographic data.
 * <p>
 * This enum serves a dual purpose within the platform:
 * <ol>
 *   <li><strong>Business Domain:</strong> Identifies the available academic campuses (Jaraguá do Sul and
 *       Joinville) and provides their internationalization (i18n) keys for localized UI representation.</li>
 *   <li><strong>System Integrity:</strong> Defines the specific IBGE codes tied to these campuses.
 *       These cities are considered "Default Cities" in the system. They are protected from updates
 *       or deletions both at the application level and via database triggers to ensure system stability.</li>
 * </ol>
 */
@Getter
@AllArgsConstructor
public enum Campi implements GenericCodes {

  /**
   * Represents the Jaraguá do Sul campus (IBGE code: 4205407).
   */
  JARAGUA_DO_SUL("4205407", "academic.campus.jaragua"),

  /**
   * Represents the Joinville campus (IBGE code: 4209106).
   */
  JOINVILLE("4209106", "academic.campus.joinville");

  /**
   * The 7-digit IBGE code corresponding to the city where the campus is located.
   */
  private final String ibgeCode;

  /**
   * The property key used to resolve the localized name of the campus.
   */
  private final String bundleKey;

  /**
   * Retrieves the IBGE codes for all registered campuses.
   * <p>
   * These codes represent default system cities. This list is utilized by the application's
   * business rules to prevent the modification or deletion of these specific cities, mirroring
   * the database-level protections (e.g., the {@code trg_protect_default_cities} trigger).
   *
   * @return a {@link List} of strings containing the immutable IBGE codes of all campuses
   */
  public static List<String> getImmutableIbgeCodes() {
    return Arrays.stream(Campi.values())
            .map(Campi::getIbgeCode)
            .collect(Collectors.toList());
  }
}