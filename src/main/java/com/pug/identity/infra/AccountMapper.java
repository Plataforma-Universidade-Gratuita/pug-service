package com.pug.identity.infra;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.AccountEntity;

/** Maps between User domain and UsersEntity persistence. */
public final class AccountMapper {
  /** Private constructor to prevent instantiation. */
  private AccountMapper() {}

  /**
   * Entity -> Domain (uses domain builder).
   *
   * @param e entity.
   * @return domain object or null if entity is null
   */
  public static Account toDomain(AccountEntity e) {
    if (e == null) {
      return null;
    }
    return Account.builder()
        .id(e.getId())
        .userId(e.getPersonId())
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
  public static AccountEntity toEntity(Account d) {
    if (d == null) {
      return null;
    }
    return AccountEntity.builder()
        .personId(d.getUserId())
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
  public static void copy(Account d, AccountEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setEmail(d.getEmail().toString());
    e.setAccountType(d.getAccountType());
    e.setPasswordHash(d.getPasswordHash());
  }
}
