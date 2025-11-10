package com.pug.identity.infra;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.UserEntity;

/** Maps between User domain and UsersEntity persistence. */
public final class UserMapper {
  private UserMapper() {}

  /**
   * Entity -> Domain (uses domain builder).
   *
   * @param e entity.
   * @return domain object or null if entity is null
   */
  public static User toDomain(UserEntity e) {
    if (e == null) {
      return null;
    }
    return User.builder()
        .id(e.getId())
        .cpf(new Cpf(e.getCpf()))
        .name(e.getName())
        .email(new Email(e.getEmail()))
        .accountType(e.getAccountType())
        .passwordHash(e.getPasswordHash())
        .createdAt(e.getCreatedAt())
        .build();
  }

  /**
   * Domain -> Entity (for persist).
   *
   * @param d domain object.
   * @return entity or null if domain is null.
   */
  public static UserEntity toEntity(User d) {
    if (d == null) {
      return null;
    }
    return UserEntity.builder()
        .cpf(d.getCpf().toString())
        .name(d.getName())
        .email(d.getEmail().toString())
        .accountType(d.getAccountType())
        .passwordHash(d.getPasswordHash())
        .createdAt(d.getCreatedAt())
        .build();
  }

  /**
   * Copy domain fields into an existing entity (for update).
   *
   * @param d domain object.
   * @param e entity to copy into.
   */
  public static void copy(User d, UserEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setCpf(d.getCpf().toString());
    e.setName(d.getName());
    e.setEmail(d.getEmail().toString());
    e.setAccountType(d.getAccountType());
    e.setPasswordHash(d.getPasswordHash());
  }
}
