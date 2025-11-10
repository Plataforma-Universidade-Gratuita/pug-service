package com.pug.identity.infra;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.persistence.AdminEntity;

/** Maps between Admin domain and AdminsEntity persistence. */
public final class AdminMapper {
  /** Private constructor to prevent instantiation. */
  private AdminMapper() {}

  /**
   * Converts an AdminsEntity to an Admin domain object.
   *
   * @param e the AdminsEntity to convert.
   * @return the corresponding Admin domain object.
   */
  public static Admin toDomain(AdminEntity e) {
    if (e == null) {
      return null;
    }
    return Admin.builder().userId(e.getUserId()).grantedAt(e.getGrantedAt()).build();
  }

  /**
   * Converts an Admin domain object to an AdminsEntity.
   *
   * @param d the Admin domain object to convert.
   * @return the corresponding AdminsEntity.
   */
  public static AdminEntity toEntity(Admin d) {
    if (d == null) {
      return null;
    }
    return AdminEntity.builder().userId(d.getUserId()).grantedAt(d.getGrantedAt()).build();
  }
}
