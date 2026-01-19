package com.pug.partner.infra;

import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.StaffEntity;

/** Maps between Staff domain and StaffEntity persistence. */
public final class StaffMapper {
  /** Private constructor to prevent instantiation. */
  private StaffMapper() {}

  /**
   * Persistence -> Domain.
   *
   * @param e StaffEntity.
   * @return Staff domain.
   */
  public static Staff toDomain(StaffEntity e) {
    if (e == null) {
      return null;
    }
    return Staff.builder().accountId(e.getAccountId()).entityId(e.getEntityId()).build();
  }

  /**
   * Domain -> Persistence (new).
   *
   * @param d Staff domain.
   * @return StaffEntity.
   */
  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    var e = new StaffEntity();
    e.setAccountId(d.getAccountId());
    e.setEntityId(d.getEntityId());
    return e;
  }
}
