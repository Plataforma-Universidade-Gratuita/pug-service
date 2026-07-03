/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.identity.infra;

import br.org.catolicasc.pug.identity.domain.Admin;
import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.persistence.AdminEntity;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;

/**
 * Stateless utility class responsible for mapping between Administrator boundary layers.
 *
 * <p>This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link
 * Admin}) does not leak into or depend upon the JPA Persistence model ({@link AdminEntity}) or the
 * Read/Query model ({@link AdminView}).
 */
public final class AdminMapper {

  private AdminMapper() {}

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
   * Translates a pure Domain {@link Admin} aggregate into a newly instantiated JPA {@link
   * AdminEntity}.
   *
   * <p>This is typically used when persisting a brand-new entity to the database.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link AdminEntity}, or {@code null} if the input domain is
   *     null
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
   * Updates an existing, attached JPA {@link AdminEntity} with the current state of a Domain {@link
   * Admin}.
   *
   * <p>Modifying the attached entity allows the ORM to track changes. The primary key (accountId)
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
   * Projects a pair of JPA entities ({@link AdminEntity} and {@link AccountEntity}) into a
   * consolidated {@link AdminView} DTO.
   *
   * <p>The resulting view nests a flattened {@link AccountView}, which exposes the linked user
   * through its {@code userId} field, along with the administrator-specific metadata ({@code
   * grantedAt} and {@code campus}).
   *
   * @param adminEntity the JPA persistence entity representing the admin privileges
   * @param accountEntity the JPA persistence entity representing the linked authentication account
   * @return a fully populated {@link AdminView} DTO, or {@code null} if either input entity is null
   */
  public static AdminView toView(AdminEntity adminEntity, AccountEntity accountEntity) {
    if (adminEntity == null || accountEntity == null) {
      return null;
    }
    AccountView accountView =
        new AccountView(
            accountEntity.getId(),
            accountEntity.getUserId(),
            accountEntity.getEmail(),
            accountEntity.getAccountType(),
            accountEntity.getCreatedAt(),
            accountEntity.getUpdatedAt(),
            accountEntity.getActive());

    return new AdminView(accountView, adminEntity.getGrantedAt(), adminEntity.getCampus());
  }
}
