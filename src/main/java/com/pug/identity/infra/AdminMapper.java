package com.pug.identity.infra;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.shared.exceptions.AppValidationException;

/**
 * Maps between Admin domain and AdminEntity persistence.
 */
public final class AdminMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private AdminMapper() {
  }

  /**
   * Maps an AdminEntity to an Admin domain object.
   *
   * @param e the AdminEntity to convert.
   * @return the corresponding Admin domain object, or null if entity is null.
   * @throws AppValidationException if the data in the entity (e.g., grantedAt) is invalid
   *                                according to domain rules, indicating corrupted data in persistence.
   */
  public static Admin toDomain(AdminEntity e) throws AppValidationException {
    if (e == null) {
      return null;
    }
    return Admin.builder().accountId(e.getAccountId()).grantedAt(e.getGrantedAt()).build();
  }

  /**
   * Maps an Admin domain object to an AdminEntity.
   *
   * @param d the Admin domain object to convert.
   * @return the corresponding AdminEntity.
   */
  public static AdminEntity toEntity(Admin d) {
    if (d == null) {
      return null;
    }
    return AdminEntity.builder().accountId(d.getAccountId()).grantedAt(d.getGrantedAt()).build();
  }
}