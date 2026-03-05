package com.pug.identity.infra;

import com.pug.identity.domain.Account;
import com.pug.identity.domain.vos.Email;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between Account boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * Account}) does not leak into or depend upon the JPA Persistence model ({@link AccountEntity}) or
 * the Read/Query model ({@link AccountView}).
 */
public final class AccountMapper {

  /** Private constructor to prevent instantiation of utility class. */
  private AccountMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Account} aggregate from a JPA {@link AccountEntity}.
   *
   * <p>This method translates primitive database columns back into their corresponding Domain Value
   * Objects (e.g., {@link Email}, {@link AuditInfo}).
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Account}, or {@code null} if the input entity is null
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
        .auditInfo(AuditInfo.factory(e.getCreatedAt(), e.getUpdatedAt()))
        .active(e.getActive())
        .build();
  }

  /**
   * Translates a pure Domain {@link Account} aggregate into a newly instantiated JPA {@link
   * AccountEntity}.
   *
   * <p>This is typically used when persisting a brand-new entity to the database. It flattens
   * Domain Value Objects back into primitive types suitable for JDBC insertion.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link AccountEntity}, or {@code null} if the input domain is
   *     null
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
        .createdAt(d.getAuditInfo().getCreatedAt())
        .updatedAt(d.getAuditInfo().getUpdatedAt())
        .active(d.getActive())
        .build();
  }

  /**
   * Updates an existing, attached JPA {@link AccountEntity} with the current state of a Domain
   * {@link Account}.
   *
   * <p>This method allows the ORM to track changes for updates. The primary key (ID) and the
   * immutable {@code userId} linkage are intentionally excluded from the copy.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Account d, AccountEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setEmail(d.getEmail().toString());
    e.setAccountType(d.getAccountType());
    e.setPasswordHash(d.getPasswordHash());
    e.setActive(d.getActive());
  }

  /**
   * Projects an {@link AccountEntity} and its parent {@link UserEntity} into a consolidated {@link
   * AccountView} DTO.
   *
   * <p>Used heavily by the query/read layer to provide a rich, nested structure ready for JSON
   * serialization without requiring complex domain logic or repeated database hits.
   *
   * @param accountEntity the JPA persistence entity representing the account
   * @param userEntity the JPA persistence entity representing the linked user
   * @return a populated {@link AccountView} DTO, or {@code null} if either input entity is null
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
            userEntity.getCreatedAt(),
            userEntity.getUpdatedAt()),
        accountEntity.getEmail(),
        accountEntity.getAccountType(),
        accountEntity.getCreatedAt(),
        accountEntity.getUpdatedAt(),
        accountEntity.getActive());
  }
}
