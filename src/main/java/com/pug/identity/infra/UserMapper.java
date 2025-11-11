package com.pug.identity.infra;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.infra.persistence.UserEntity;

/** Mapper for User entity and domain object. */
public final class UserMapper {
  /** Private constructor to prevent instantiation. */
  private UserMapper() {}

  /**
   * Entity -> Domain (uses domain builder).
   *
   * @param e entity
   * @return domain object or null if entity is null
   */
  public static User toDomain(UserEntity e) {
    if (e == null) {
      return null;
    }

    return User.builder()
        .id(e.getId())
        .name(e.getName())
        .cpf(new Cpf(e.getCpf()))
        .createdAt(e.getCreatedAt())
        .build();
  }

  /**
   * Domain -> Entity (for persist).
   *
   * @param d domain object
   * @return entity or null if domain is null
   */
  public static UserEntity toEntity(User d) {
    if (d == null) {
      return null;
    }

    return UserEntity.builder()
        .cpf(d.getCpf().toString())
        .name(d.getName())
        .createdAt(d.getCreatedAt())
        .build();
  }

  /**
   * Copy domain fields into an existing entity (for update).
   *
   * @param d domain object
   * @param e entity to update
   */
  public static void copy(User d, UserEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setCpf(d.getCpf().toString());
  }
}
