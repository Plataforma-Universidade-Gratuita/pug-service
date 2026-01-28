package com.pug.identity.infra;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;

/**
 * Maps between Account domain and AccountEntity persistence.
 */
public final class AccountMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private AccountMapper() {
  }

  /**
   * Maps an AccountEntity to an Account domain object.
   *
   * @param e the AccountEntity.
   * @return the Account domain object, or null if entity is null.
   */
  public static Account toDomain(AccountEntity e) {
    if (e == null) {
      return null;
    }
    return Account.builder()
            .id(e.getId())
            .userId(e.getUserId())
            .email(Email.factory(e.getEmail()))
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

  /**
   * Converts an AccountEntity and its associated UserEntity to an AccountView.
   *
   * @param accountEntity the AccountEntity.
   * @param userEntity    the associated UserEntity.
   * @return the AccountView.
   */
  public static AccountView toView(AccountEntity accountEntity, UserEntity userEntity) {
    if (accountEntity == null || userEntity == null) {
      return null;
    }
    return new AccountView(
            accountEntity.getId(),
            new UserView(
                    userEntity.getId(),
                    userEntity.getCpf(),
                    userEntity.getName(),
                    userEntity.getCreatedAt()),
            accountEntity.getEmail(),
            accountEntity.getAccountType(),
            accountEntity.getCreatedAt());
  }
}
