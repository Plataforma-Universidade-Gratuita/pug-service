package com.pug.partner.infra;

import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.StaffEntity;

public final class StaffMapper {
  private StaffMapper() {}

  public static Staff toDomain(StaffEntity e) {
    if (e == null) return null;
    return Staff.builder()
        .id(e.getId())
        .userId(e.getUserId())
        .email(e.getEmail())
        .entityId(e.getEntityId())
        .active(e.isActive())
        .build();
  }

  public static StaffEntity toEntity(Staff d) {
    if (d == null) return null;
    return StaffEntity.builder()
        .id(d.getId())
        .userId(d.getUserId())
        .email(d.getEmail())
        .entityId(d.getEntityId())
        .active(d.isActive())
        .build();
  }

  /** Copy domain fields into a managed entity (no id touch). */
  public static void copy(Staff d, StaffEntity e) {
    e.setUserId(d.getUserId());
    e.setEmail(d.getEmail());
    e.setEntityId(d.getEntityId());
    e.setActive(d.isActive());
  }
}
