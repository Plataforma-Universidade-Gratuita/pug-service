package com.pug.identity.infra;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.infra.read.dtos.UserView;

/** Maps between Admin domain and AdminEntity persistence. */
public final class AdminMapper {
  /** Private constructor to prevent instantiation. */
  private AdminMapper() {}

  /**
   * Maps an AdminEntity to an Admin domain object.
   *
   * @param e the AdminEntity to convert.
   * @return the corresponding Admin domain object, or null if entity is null.
   */
  public static Admin toDomain(AdminEntity e) {
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

  /**
   * Converts an AdminEntity, AccountEntity, and UserEntity into an AdminView.
   *
   * @param adminEntity the AdminEntity.
   * @param accountEntity the associated AccountEntity.
   * @param userEntity the associated UserEntity.
   * @return the AdminView.
   */
  public static AdminView toView(
      AdminEntity adminEntity, AccountEntity accountEntity, UserEntity userEntity) {
    return new AdminView(
        new AccountView(
            accountEntity.getId(),
            new UserView(
                userEntity.getId(),
                userEntity.getCpf(),
                userEntity.getName(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()),
            accountEntity.getEmail(),
            accountEntity.getAccountType(),
            accountEntity.getCreatedAt(),
            accountEntity.getUpdatedAt()),
        adminEntity.getGrantedAt());
  }
}
