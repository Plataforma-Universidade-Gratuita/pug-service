package com.pug.partner.infra;

import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.StaffEntity;
import com.pug.shared.exceptions.AppValidationException;

/** Maps between Staff domain and StaffEntity persistence. */
public final class StaffMapper {
  /** Private constructor to prevent instantiation. */
  private StaffMapper() {}

  /**
   * Maps a StaffEntity to a Staff domain object.
   *
   * @param e the StaffEntity.
   * @return the Staff domain object, or null if entity is null.
   * @throws AppValidationException if the data in the entity (e.g., accountId or entityId) is
   *     invalid according to domain rules, indicating corrupted data in persistence.
   */
  public static Staff toDomain(StaffEntity e) throws AppValidationException {
    if (e == null) {
      return null;
    }
    return Staff.builder().accountId(e.getAccountId()).entityId(e.getEntityId()).build();
  }

  /**
   * Maps a Staff domain object to a StaffEntity for persistence.
   *
   * @param d the Staff domain object.
   * @return the StaffEntity, or null if domain is null.
   */
  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    return StaffEntity.builder().accountId(d.getAccountId()).entityId(d.getEntityId()).build();
  }

  /**
   * Copies domain fields into an existing StaffEntity (for update).
   *
   * @param d the Staff domain object.
   * @param e the StaffEntity to copy into.
   */
  public static void copy(Staff d, StaffEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setEntityId(d.getEntityId());
  }
}
