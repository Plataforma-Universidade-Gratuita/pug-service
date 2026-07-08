package br.org.catolicasc.pug.shared.infra.audit;

/**
 * Represents a granular change for a specific domain field.
 *
 * @param fieldName the name of the field that changed
 * @param oldValue the original value of the field before the modification
 * @param newValue the new value of the field after the modification
 */
public record FieldChange(String fieldName, String oldValue, String newValue) {

  /**
   * Constructs a {@code FieldChange} by converting the provided old and new values to strings.
   *
   * @param fieldName the name of the field that changed
   * @param oldValue the original value of the field before the modification, which will be
   *     converted to a string
   * @param newValue the new value of the field after the modification, which will be converted to a
   *     string
   */
  public FieldChange(String fieldName, Object oldValue, Object newValue) {
    this(
        fieldName,
        oldValue != null ? oldValue.toString() : null,
        newValue != null ? newValue.toString() : null);
  }
}
