package com.pug.partner.infra;

import com.pug.identity.domain.User;
import com.pug.identity.infra.UserMapper;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.EntitiesEntity;
import com.pug.partner.infra.persistence.StaffEntity;

/**
 * Mapper class for converting between Staff domain objects and StaffEntity persistence entities.
 */
public final class StaffMapper {
  /** Private constructor to prevent instantiation. */
  private StaffMapper() {}

  /**
   * Convert StaffEntity to Staff domain object.
   *
   * @param e the StaffEntity to convert.
   * @return the corresponding Staff domain object.
   */
  public static Staff toDomain(StaffEntity e) {
    if (e == null) {
      return null;
    }

    User user =
        (e.getUser() != null)
            ? UserMapper.toDomain(e.getUser())
            : User.builder().id(e.getUserId()).build();

    Entity entity =
        (e.getEntity() != null)
            ? EntityMapper.toDomain(e.getEntity())
            : Entity.builder().id(e.getEntityId()).build();

    return Staff.builder().user(user).entity(entity).build();
  }

  /**
   * Convert Staff domain object to StaffEntity.
   *
   * @param d the Staff domain object to convert.
   * @return the corresponding StaffEntity.
   */
  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    var se = new StaffEntity();
    se.setUserId(d.getUser().getId());
    se.setEntityId(d.getEntity().getId());

    var ue = UsersEntity.builder().build();
    ue.setId(d.getUser().getId());
    se.setUser(ue);

    var ee = new EntitiesEntity();
    ee.setId(d.getEntity().getId());
    se.setEntity(ee);

    return se;
  }

  /**
   * Copy domain Staff data into existing StaffEntity.
   *
   * @param d the domain Staff data to copy from
   * @param e the StaffEntity to copy into
   */
  public static void copy(Staff d, StaffEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setEntityId(d.getEntity().getId());
    var ee = new EntitiesEntity();
    ee.setId(d.getEntity().getId());
    e.setEntity(ee);
  }
}
