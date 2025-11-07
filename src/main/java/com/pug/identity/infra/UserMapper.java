package com.pug.identity.infra;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.UsersEntity;
import com.pug.shared.domain.enums.AccountType;
import java.util.Locale;

/** Maps between User domain and UsersEntity persistence. */
public final class UserMapper {
  /** Private constructor to prevent instantiation. */
  private UserMapper() {}

  /**
   * Converts a UsersEntity to a User domain object.
   *
   * @param e the UsersEntity.
   * @return the corresponding User domain object.
   */
  public static User toDomain(UsersEntity e) {
    if (e == null) {
      return null;
    }
    return User.builder()
        .id(e.getId())
        .cpf(new Cpf(e.getCpf()))
        .name(e.getName())
        .email(new Email(e.getEmail()))
        .accountType(parseAccountType(e.getAccountType()))
        .passwordHash(e.getPasswordHash())
        .active(e.getActive())
        .createdAt(e.getCreatedAt())
        .build();
  }

  /**
   * Converts a User domain object to a UsersEntity for persistence.
   *
   * @param d the User domain object.
   * @return the corresponding UsersEntity.
   */
  public static UsersEntity toEntity(User d) {
    if (d == null) {
      return null;
    }
    return UsersEntity.builder()
        .cpf(d.getCpf().toString())
        .name(d.getName())
        .email(d.getEmail().toString())
        .accountType(d.getAccountType().name())
        .passwordHash(d.getPasswordHash())
        .active(d.getActive() == null ? Boolean.TRUE : d.getActive())
        .build();
  }

  /**
   * Copies fields from a User domain object to an existing UsersEntity.
   *
   * @param d the User domain object.
   * @param e the UsersEntity to copy data into.
   */
  public static void copy(User d, UsersEntity e) {
    e.setCpf(d.getCpf().toString());
    e.setName(d.getName());
    e.setEmail(d.getEmail().toString());
    e.setAccountType(d.getAccountType().name());
    e.setPasswordHash(d.getPasswordHash());
    e.setActive(d.getActive());
  }

  /**
   * Parses a string to an AccountType enum, handling nulls and case insensitivity.
   *
   * @param v the string to parse.
   * @return the corresponding AccountType enum or null if input is null.
   */
  private static AccountType parseAccountType(String v) {
    return (v == null) ? null : AccountType.valueOf(v.trim().toUpperCase(Locale.ROOT));
  }
}
