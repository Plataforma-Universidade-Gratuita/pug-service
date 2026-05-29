package br.org.catolicasc.pug.shared.infra.persistence;

import br.org.catolicasc.pug.shared.utils.StringUtils;

/** Shared helpers for simple database-backed text filtering in JPQL queries. */
public final class JpaSearchUtils {

  private static final String LATIN_ACCENTS = "ÁÀÃÂÄáàãâäÉÈÊËéèêëÍÌÎÏíìîïÓÒÕÔÖóòõôöÚÙÛÜúùûüÇç";
  private static final String ASCII_EQUIVALENTS = "AAAAAaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUUuuuuCc";

  private JpaSearchUtils() {}

  /**
   * Builds a JPQL expression that lowercases and folds a text field through PostgreSQL
   * {@code translate(...)} so accent-insensitive {@code like} matching can stay database-backed.
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

  /** Converts a raw user query into a folded {@code %contains%} pattern. */
  public static String containsPattern(String rawQuery) {
    return "%" + StringUtils.fold(rawQuery) + "%";
  }
}
