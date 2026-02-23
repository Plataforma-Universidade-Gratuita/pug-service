package com.pug.shared.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration representing the University Campuses and their corresponding geographic data.
 *
 * <p>This enum serves a dual purpose:
 *
 * <ol>
 *   <li><strong>Business Domain:</strong> Identifies the available campuses (Jaraguá do Sul and
 *       Joinville) and provides their internationalization keys.
 *   <li><strong>System Integrity:</strong> Defines the specific IBGE codes that are considered
 *       "Default Cities". These records are immutable and protected from updates or deletions to
 *       ensure system stability.
 * </ol>
 */
@Getter
@AllArgsConstructor
public enum Campi {
  JARAGUA_DO_SUL("4205407", "academic.campus.jaragua"),
  JOINVILLE("4209106", "academic.campus.joinville");

  private final String ibgeCode;
  private final String bundleKey;

  /**
   * Retrieves the IBGE codes for all Campuses, which are treated as immutable system records.
   *
   * <p>This list is utilized by business rules to prevent the modification or deletion of these
   * specific cities in the database.
   *
   * @return a {@link List} of strings containing the IBGE codes of all campuses.
   */
  public static List<String> getImmutableIbgeCodes() {
    return Arrays.stream(Campi.values()).map(Campi::getIbgeCode).collect(Collectors.toList());
  }
}
