package com.pug.identity.infra;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.persistence.AdminEntity;

public final class AdminMapper {
  private AdminMapper() {}

  public static Admin toDomain(AdminEntity e) {
    if (e == null) return null;
    return new Admin(e.getUserId(), e.getGrantedAt());
  }
}
