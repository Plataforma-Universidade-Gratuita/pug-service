package com.pug.identity.infra;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.domain.vos.AuditInfo;

/** Maps between User domain and UserEntity persistence. */
public final class UserMapper {
  /** Private constructor to prevent instantiation. */
  private UserMapper() {}

  /**
   * Maps a UserEntity to a User domain object.
   *
   * @param e the UserEntity.
   * @return the User domain object, or null if entity is null.
   */
  public static User toDomain(UserEntity e) {
    if (e == null) {
      return null;
    }
    return User.builder()
        .id(e.getId())
        .name(e.getName())
        .cpf(Cpf.factory(e.getCpf()))
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .build();
  }

  /**
   * Maps a User domain object to a UserEntity for persistence.
   *
   * @param d the User domain object.
   * @return the UserEntity, or null if domain is null.
   */
  public static UserEntity toEntity(User d) {
    if (d == null) {
      return null;
    }
    return UserEntity.builder()
        .id(d.getId())
        .cpf(d.getCpf().toString())
        .name(d.getName())
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .build();
  }

  /**
   * Copies domain fields into an existing UserEntity (for update).
   *
   * @param d the User domain object.
   * @param e the UserEntity to copy into.
   */
  public static void copy(User d, UserEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setCpf(d.getCpf().toString());
  }

  /**
   * Converts a UserEntity to a UserView.
   *
   * @param e the UserEntity
   * @return the UserView
   */
  public static UserView toView(UserEntity e) {
    if (e == null) {
      return null;
    }
    return new UserView(e.getId(), e.getCpf(), e.getName(), e.getCreatedAt(), e.getUpdatedAt());
  }
}
