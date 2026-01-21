package com.pug.identity.infra;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.shared.exceptions.AppValidationException;

/** Maps between Account domain and AccountEntity persistence. */
public final class AccountMapper {
  /** Private constructor to prevent instantiation. */
  private AccountMapper() {}

  /**
   * Maps an AccountEntity to an Account domain object.
   *
   * @param e the AccountEntity.
   * @return the Account domain object, or null if entity is null.
   * @throws AppValidationException if the data in the entity (e.g., email) is invalid according to
   *     domain rules, indicating corrupted data in persistence.
   */
  public static Account toDomain(AccountEntity e) throws AppValidationException {
    if (e == null) {
      return null;
    }
    return Account.builder()
        .id(e.getId())
        .userId(e.getUserId())
        .email(new Email(e.getEmail()))
        .accountType(e.getAccountType())
        .passwordHash(e.getPasswordHash())
        .createdAt(e.getCreatedAt())
        .build();
  }

  /**
   * Maps an Account domain object to an AccountEntity for persistence.
   *
   * @param d the Account domain object.
   * @return the AccountEntity, or null if domain is null.
   */
  public static AccountEntity toEntity(Account d) {
    if (d == null) {
      return null;
    }
    return AccountEntity.builder()
        .id(d.getId())
        .userId(d.getUserId())
        .email(d.getEmail().toString())
        .accountType(d.getAccountType())
        .passwordHash(d.getPasswordHash())
        .createdAt(d.getCreatedAt())
        .build();
  }

  /**
   * Copies domain fields into an existing AccountEntity (for update).
   *
   * @param d the Account domain object.
   * @param e the AccountEntity to copy into.
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
