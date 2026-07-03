/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.infra.persistence;

import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.persistence.TypedQuery;

/**
 * Shared helper methods for database-backed text search in JPQL queries.
 *
 * <p>This utility centralizes the repository's accent-insensitive matching rules so read-side
 * queries can reuse the same normalization behavior across modules. The generated expressions rely
 * on PostgreSQL {@code translate(...)} plus lowercase conversion, which keeps filtering in the
 * database while still matching folded user input produced by {@link StringUtils#fold(String)}.
 */
public final class JpaSearchUtils {

  private static final String LATIN_ACCENTS = "ÁÀÃÂÄáàãâäÉÈÊËéèêëÍÌÎÏíìîïÓÒÕÔÖóòõôöÚÙÛÜúùûüÇç";
  private static final String ASCII_EQUIVALENTS = "AAAAAaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUUuuuuCc";

  private JpaSearchUtils() {}

  /**
   * Builds a JPQL expression that lowercases and folds a text field through PostgreSQL {@code
   * translate(...)} so accent-insensitive {@code like} matching can stay database-backed.
   *
   * @param fieldPath the JPQL path to the text field being searched
   * @return the folded JPQL expression for the provided field
   */
  public static String folded(String fieldPath) {
    return "lower(function('translate', "
        + fieldPath
        + ", '"
        + LATIN_ACCENTS
        + "', '"
        + ASCII_EQUIVALENTS
        + "'))";
  }

  /**
   * Builds an accent-insensitive {@code like} clause for the provided field and parameter name.
   *
   * <p>This helper is intended for optional contains-style filters where the query binds a folded
   * pattern separately through {@link #bindContains(TypedQuery, String, String)}.
   *
   * @param fieldPath the JPQL path to the text field being searched
   * @param parameterName the named query parameter that will receive the folded pattern
   * @return a complete JPQL {@code like} clause for the provided field and parameter
   */
  public static String containsClause(String fieldPath, String parameterName) {
    return folded(fieldPath) + " like :" + parameterName;
  }

  /**
   * Converts a raw user query into a folded {@code %contains%} pattern.
   *
   * @param rawQuery the raw user input to normalize
   * @return a folded pattern suitable for JPQL {@code like} bindings
   */
  public static String containsPattern(String rawQuery) {
    return "%" + StringUtils.fold(rawQuery) + "%";
  }

  /**
   * Binds a folded {@code %contains%} pattern to the provided query parameter.
   *
   * @param query the query receiving the bound parameter
   * @param parameterName the named parameter to bind
   * @param rawQuery the raw user input to normalize before binding
   * @param <T> the query result type
   */
  public static <T> void bindContains(TypedQuery<T> query, String parameterName, String rawQuery) {
    query.setParameter(parameterName, containsPattern(rawQuery));
  }
}
