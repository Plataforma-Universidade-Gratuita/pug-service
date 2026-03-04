package com.pug.identity.infra;

import com.pug.identity.domain.Admin;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.AdminEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.AdminView;
import com.pug.identity.infra.read.dtos.UserView;

/**
 * Stateless utility class responsible for mapping between Administrator boundary layers.
 * <p>
 * This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link Admin})
 * does not leak into or depend upon the JPA Persistence model ({@link AdminEntity}) or the
 * Read/Query model ({@link AdminView}).
 */
public final class AdminMapper {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private AdminMapper() {
  }

  /**
   * Reconstitutes a pure Domain {@link Admin} aggregate from a JPA {@link AdminEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Admin}, or {@code null} if the input entity is null
   */
  public static Admin toDomain(AdminEntity e) {
    if (e == null) {
      return null;
    }
    return Admin.builder()
            .accountId(e.getAccountId())
            .grantedAt(e.getGrantedAt())
            .campus(e.getCampus())
            .build();
  }

  /**
   * Translates a pure Domain {@link Admin} aggregate into a newly instantiated JPA {@link AdminEntity}.
   * <p>
   * This is typically used when persisting a brand-new entity to the database.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link AdminEntity}, or {@code null} if the input domain is null
   */
  public static AdminEntity toEntity(Admin d) {
    if (d == null) {
      return null;
    }
    return AdminEntity.builder()
            .accountId(d.getAccountId())
            .grantedAt(d.getGrantedAt())
            .campus(d.getCampus())
            .build();
  }

  /**
   * Updates an existing, attached JPA {@link AdminEntity} with the current state of a Domain {@link Admin}.
   * <p>
   * Modifying the attached entity allows the ORM to track changes. The primary key (accountId)
   * and immutable timestamps are excluded from the copy.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(Admin d, AdminEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setCampus(d.getCampus());
  }

  /**
   * Projects a deeply nested set of JPA Entities (Admin, Account, User) into a comprehensive
   * {@link AdminView} DTO.
   * <p>
   * Used heavily by the CQRS query layer to construct fully resolved data structures
   * that encapsulate the administrator's profile, credentials, and identity in a single view.
   *
   * @param adminEntity   the JPA persistence entity representing the admin privileges
   * @param accountEntity the JPA persistence entity representing the linked account
   * @param userEntity    the JPA persistence entity representing the linked user
   * @return a fully populated {@link AdminView} DTO
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
            adminEntity.getGrantedAt(),
            adminEntity.getCampus());
  }
}