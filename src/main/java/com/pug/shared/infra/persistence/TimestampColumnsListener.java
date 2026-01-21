package com.pug.shared.infra.persistence;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import lombok.Getter;
import org.jboss.logging.Logger;

/**
 * JPA Entity Listener to automatically manage timestamp columns such as createdAt, updatedAt,
 * closedAt, etc., based on entity lifecycle events and status changes.
 */
public class TimestampColumnsListener {
  private static final Logger LOG = Logger.getLogger(TimestampColumnsListener.class);

  @Getter
  private enum FieldNames {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    GRANTED_AT("grantedAt"),
    REQUEST_AT("requestAt"),
    CLOSED_AT("closedAt"),
    ACCEPTED_AT("acceptedAt"),
    CLOSING_STATUS_AT("closingStatusAt"),
    VALIDATED_AT("validatedAt"),
    STATUS("status");

    private final String fieldName;

    /**
     * Constructor for FieldNames enum.
     *
     * @param fieldName The name of the field.
     */
    FieldNames(String fieldName) {
      this.fieldName = fieldName;
    }
  }

  private static final Set<String> ON_CREATE =
      Set.of(
          FieldNames.CREATED_AT.getFieldName(),
          FieldNames.GRANTED_AT.getFieldName(),
          FieldNames.REQUEST_AT.getFieldName());
  private static final Set<String> ON_UPDATE = Set.of(FieldNames.UPDATED_AT.getFieldName());

  private static final Set<String> PROJECT_CLOSED_STATUSES = Set.of("INTERRUPTED", "CONCLUDED");
  private static final Set<String> ENROLLMENT_ACCEPTED = Set.of("ACCEPTED");
  private static final Set<String> ENROLLMENT_CLOSING_STATUSES =
      Set.of("DECLINED", "EXITED", "REMOVED", "INTERRUPTED", "CONCLUDED");
  private static final Set<String> ATTENDANCE_VALIDATED = Set.of("VALIDATED");

  /**
   * Sets timestamp fields before persisting a new entity.
   *
   * @param entity the entity being persisted.
   */
  @PrePersist
  public void prePersist(Object entity) {
    OffsetDateTime now = OffsetDateTime.now();

    for (String f : ON_CREATE) {
      setIfNull(entity, f, now);
    }

    applyStatusDrivenTimestamps(entity, now);
  }

  /**
   * Sets timestamp fields before updating an existing entity.
   *
   * @param entity the entity being updated.
   */
  @PreUpdate
  public void preUpdate(Object entity) {
    OffsetDateTime now = OffsetDateTime.now();

    for (String f : ON_UPDATE) {
      setIfPresent(entity, f, now);
    }

    applyStatusDrivenTimestamps(entity, now);
  }

  /**
   * Applies timestamp updates based on the entity's status field.
   *
   * @param entity the entity being processed.
   * @param now the current timestamp.
   */
  private void applyStatusDrivenTimestamps(Object entity, OffsetDateTime now) {
    String status = readStatus(entity);
    if (status == null) {
      return;
    }

    if (PROJECT_CLOSED_STATUSES.contains(status)
        && hasField(entity, FieldNames.CLOSED_AT.getFieldName())) {
      set(entity, FieldNames.CLOSED_AT.getFieldName(), now);
    }

    if (ENROLLMENT_ACCEPTED.contains(status)
        && hasField(entity, FieldNames.ACCEPTED_AT.getFieldName())) {
      set(entity, FieldNames.ACCEPTED_AT.getFieldName(), now);
    }

    if (ENROLLMENT_CLOSING_STATUSES.contains(status)
        && hasField(entity, FieldNames.CLOSING_STATUS_AT.getFieldName())) {
      set(entity, FieldNames.CLOSING_STATUS_AT.getFieldName(), now);
    }

    if (ATTENDANCE_VALIDATED.contains(status)
        && hasField(entity, FieldNames.VALIDATED_AT.getFieldName())) {
      set(entity, FieldNames.VALIDATED_AT.getFieldName(), now);
    }
  }

  /**
   * Reads the status field from the entity using reflection.
   *
   * @param entity the entity from which to read the status.
   * @return the status as a string, or null if not found or inaccessible.
   */
  private String readStatus(Object entity) {
    Field f = find(entity.getClass(), FieldNames.STATUS.getFieldName());
    if (f == null) {
      return null;
    }
    try {
      f.setAccessible(true);
      Object v = f.get(entity);
      if (v == null) {
        return null;
      }
      if (v instanceof Enum<?> e) {
        return e.name();
      }
      return v.toString().trim().toUpperCase(Locale.ROOT);
    } catch (IllegalAccessException e) {
      LOG.debugf(
          "Unable to read field 'status' on %s: %s", entity.getClass().getName(), e.getMessage());
      return null;
    }
  }

  /**
   * Sets the field to the given value if it is currently null.
   *
   * @param target the target object.
   * @param fieldName the name of the field to set.
   * @param value the value to set.
   */
  private static void setIfNull(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) {
      return;
    }
    try {
      f.setAccessible(true);
      if (f.get(target) == null) {
        f.set(target, value);
      }
    } catch (IllegalAccessException e) {
      LOG.debugf(
          "Unable to setIfNull '%s' on %s: %s",
          fieldName, target.getClass().getName(), e.getMessage());
    }
  }

  /**
   * Sets the field to the given value if the field is present.
   *
   * @param target the target object.
   * @param fieldName the name of the field to set.
   * @param value the value to set.
   */
  private static void setIfPresent(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) {
      return;
    }
    try {
      f.setAccessible(true);
      f.set(target, value);
    } catch (IllegalAccessException e) {
      LOG.debugf(
          "Unable to setIfNull '%s' on %s: %s",
          fieldName, target.getClass().getName(), e.getMessage());
    }
  }

  /**
   * Sets the field to the given value.
   *
   * @param target the target object.
   * @param fieldName the name of the field to set.
   * @param value the value to set.
   */
  private static void set(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) {
      return;
    }
    try {
      f.setAccessible(true);
      Object cur = f.get(target);
      if (cur == null) {
        f.set(target, value);
      }
    } catch (IllegalAccessException e) {
      LOG.debugf(
          "Unable to setIfNull '%s' on %s: %s",
          fieldName, target.getClass().getName(), e.getMessage());
    }
  }

  /**
   * Checks if the target object has the specified field.
   *
   * @param target the target object.
   * @param fieldName the name of the field to check.
   * @return true if the field exists, false otherwise.
   */
  private static boolean hasField(Object target, String fieldName) {
    return find(target.getClass(), fieldName) != null;
  }

  /**
   * Finds a field in the given class or its superclasses.
   *
   * @param c the class to search.
   * @param fieldName the name of the field to find.
   * @return the Field object if found, null otherwise.
   */
  private static Field find(Class<?> c, String fieldName) {
    Class<?> cur = c;
    while (cur != null && cur != Object.class) {
      try {
        return cur.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        cur = cur.getSuperclass();
      }
    }
    return null;
  }
}
