package br.org.catolicasc.pug.shared.utils;

import br.org.catolicasc.pug.shared.infra.audit.FieldChange;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for calculating the differences between two objects using reflection.
 *
 * <p>This class provides mechanisms to inspect two domain objects and determine which specific
 * fields have changed, making it ideal for auditing state transitions in an immutable domain model.
 */
public final class DiffUtils {

  /** Private constructor to prevent instantiation of utility class. */
  private DiffUtils() {}

  /**
   * Defines fields that should be excluded from audit logs due to security, privacy, or technical
   * reasons.
   */
  private enum Ignored {
    PASSWORD_HASH("passwordHash"),
    QR_VALIDATION_HASH("qrValidationHash"),
    EMAIL("email"),
    CPF("cpf"),
    CNPJ("cnpj"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    Ignored(String fieldName) {
      this.fieldName = fieldName;
    }

    public static Set<String> all() {
      return Set.of(
          PASSWORD_HASH.fieldName,
          QR_VALIDATION_HASH.fieldName,
          EMAIL.fieldName,
          CPF.fieldName,
          CNPJ.fieldName,
          CREATED_AT.fieldName,
          UPDATED_AT.fieldName);
    }
  }

  /**
   * Compares two objects of the same type and returns a map of changed fields.
   *
   * <p>The map uses the field name as the key and a {@link FieldChange} record containing the old
   * and new values as the value. Fields explicitly listed in the {@code ignoredFields} set are
   * skipped during comparison, which is useful for filtering sensitive data like password hashes.
   *
   * @param oldObj the original state of the object
   * @param newObj the new state of the object
   * @return a {@link Map} where keys are field names and values are the {@link FieldChange} records
   */
  public static Map<String, FieldChange> diff(Object oldObj, Object newObj) {
    Map<String, FieldChange> diffs = new HashMap<>();
    if (oldObj == null || newObj == null) {
      return diffs;
    }

    for (Field field : oldObj.getClass().getDeclaredFields()) {
      if (Ignored.all().contains(field.getName())) {
        continue;
      }

      field.setAccessible(true);
      try {
        Object oldVal = field.get(oldObj);
        Object newVal = field.get(newObj);

        if (hasChanged(oldVal, newVal)) {
          diffs.put(field.getName(), new FieldChange(oldVal, newVal));
        }
      } catch (IllegalAccessException e) {
        // Silently skip fields that cannot be accessed via reflection
      }
    }
    return diffs;
  }

  /**
   * Helper method to determine if two values are different.
   *
   * @param oldVal the original value
   * @param newVal the new value
   * @return {@code true} if the values are different, {@code false} otherwise
   */
  private static boolean hasChanged(Object oldVal, Object newVal) {
    if (oldVal == null && newVal == null) {
      return false;
    }
    if (oldVal == null || newVal == null) {
      return true;
    }
    return !oldVal.equals(newVal);
  }
}
