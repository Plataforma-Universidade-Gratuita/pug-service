/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.shared.utils;

import br.org.catolicasc.pug.shared.infra.audit.FieldChange;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Utility class for calculating the differences between two objects using reflection.
 *
 * <p>This class provides mechanisms to inspect two domain objects and determine which specific
 * fields have changed, making it ideal for auditing state transitions in an immutable domain model.
 * Complex nested objects are recursively diffed with dot-notation field names.
 */
@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
public final class DiffUtils {

  private DiffUtils() {}

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

    /** Returns the set of field names that must be excluded from reflective diffing. */
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
   * <p>Complex nested objects are recursively diffed, producing dot-notation keys (e.g., {@code
   * "address.street"}). Primitive types, enums, strings, UUIDs, and temporal types are compared
   * directly and their values are stored as strings.
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
    diffRecursive("", oldObj, newObj, diffs);
    return diffs;
  }

  private static void diffRecursive(
      String prefix, Object oldObj, Object newObj, Map<String, FieldChange> diffs) {
    for (Field field : oldObj.getClass().getDeclaredFields()) {
      String qualifiedName = prefix.isEmpty() ? field.getName() : prefix + "." + field.getName();

      if (Ignored.all().contains(field.getName())) {
        continue;
      }

      field.setAccessible(true);
      try {
        Object oldVal = field.get(oldObj);
        Object newVal = field.get(newObj);

        if (!hasChanged(oldVal, newVal)) {
          continue;
        }

        if (isLeafType(field.getType())) {
          diffs.put(qualifiedName, new FieldChange(qualifiedName, oldVal, newVal));
        } else if (oldVal != null
            && newVal != null
            && oldVal.getClass().equals(newVal.getClass())) {
          // Recurse into nested complex objects
          diffRecursive(qualifiedName, oldVal, newVal, diffs);
        } else {
          // One is null or types differ — store as string representation
          diffs.put(qualifiedName, new FieldChange(qualifiedName, oldVal, newVal));
        }
      } catch (IllegalAccessException e) {
        // Silently skip fields that cannot be accessed via reflection
      }
    }
  }

  /** Determines whether a type should be compared directly (leaf) vs recursed into. */
  private static boolean isLeafType(Class<?> type) {
    return type.isPrimitive()
        || type.isEnum()
        || CharSequence.class.isAssignableFrom(type)
        || Number.class.isAssignableFrom(type)
        || Boolean.class.equals(type)
        || UUID.class.equals(type)
        || Temporal.class.isAssignableFrom(type)
        || type.getName().startsWith("java.");
  }

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
