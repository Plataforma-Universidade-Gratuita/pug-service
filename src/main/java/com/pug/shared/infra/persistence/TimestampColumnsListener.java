package com.pug.shared.infra.persistence;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import lombok.Getter;

public class TimestampColumnsListener {
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

  @PrePersist
  public void prePersist(Object entity) {
    OffsetDateTime now = OffsetDateTime.now();

    for (String f : ON_CREATE) {
      setIfNull(entity, f, now);
    }

    applyStatusDrivenTimestamps(entity, now);
  }

  @PreUpdate
  public void preUpdate(Object entity) {
    OffsetDateTime now = OffsetDateTime.now();

    for (String f : ON_UPDATE) {
      setIfPresent(entity, f, now);
    }

    applyStatusDrivenTimestamps(entity, now);
  }

  private void applyStatusDrivenTimestamps(Object entity, OffsetDateTime now) {
    String status = readStatus(entity);
    if (status == null) return;

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

  private String readStatus(Object entity) {
    Field f = find(entity.getClass(), FieldNames.STATUS.getFieldName());
    if (f == null) return null;
    try {
      f.setAccessible(true);
      Object v = f.get(entity);
      if (v == null) return null;
      if (v instanceof Enum<?> e) return e.name();
      return v.toString().trim().toUpperCase(Locale.ROOT);
    } catch (IllegalAccessException ignored) {
      return null;
    }
  }

  private static void setIfNull(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) return;
    try {
      f.setAccessible(true);
      if (f.get(target) == null) f.set(target, value);
    } catch (IllegalAccessException ignored) {
    }
  }

  private static void setIfPresent(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) return;
    try {
      f.setAccessible(true);
      f.set(target, value);
    } catch (IllegalAccessException ignored) {
    }
  }

  private static void set(Object target, String fieldName, OffsetDateTime value) {
    Field f = find(target.getClass(), fieldName);
    if (f == null) return;
    try {
      f.setAccessible(true);
      Object cur = f.get(target);
      if (cur == null) f.set(target, value);
    } catch (IllegalAccessException ignored) {
    }
  }

  private static boolean hasField(Object target, String fieldName) {
    return find(target.getClass(), fieldName) != null;
  }

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
