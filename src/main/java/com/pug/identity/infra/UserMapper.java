package com.pug.identity.infra;

import com.pug.identity.domain.User;
import com.pug.identity.domain.vos.Cpf;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.shared.domain.vos.AuditInfo;

/**
 * Stateless utility class responsible for mapping between User boundary layers.
 * <p>
 * This mapper acts as an anti-corruption layer, ensuring that the pure Domain model ({@link User})
 * does not leak into or depend upon the JPA Persistence model ({@link UserEntity}) or the
 * Read/Query model ({@link UserView}).
 */
public final class UserMapper {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private UserMapper() {
  }

  /**
   * Reconstitutes a pure Domain {@link User} aggregate from a JPA {@link UserEntity}.
   * <p>
   * This method translates primitive database columns back into their corresponding
   * Domain Value Objects (e.g., using {@link Cpf#factory(String)} and {@link AuditInfo}).
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link User}, or {@code null} if the input entity is null
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
   * Translates a pure Domain {@link User} aggregate into a newly instantiated JPA {@link UserEntity}.
   * <p>
   * This is typically used when persisting a brand-new entity to the database. It flattens
   * Domain Value Objects back into primitive types suitable for JDBC insertion.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link UserEntity}, or {@code null} if the input domain is null
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
   * Updates an existing, attached JPA {@link UserEntity} with the current state of a Domain {@link User}.
   * <p>
   * This method is used during update operations. Modifying the attached entity allows the
   * ORM (Hibernate) to track changes and issue the appropriate SQL {@code UPDATE} statements
   * upon transaction commit. Primary keys and immutable audit fields are intentionally excluded.
   *
   * @param d the Domain aggregate containing the updated state
   * @param e the existing, attached JPA entity to update in-place
   */
  public static void copy(User d, UserEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setName(d.getName());
    e.setCpf(d.getCpf().toString());
  }

  /**
   * Projects a JPA {@link UserEntity} into a lightweight, read-only {@link UserView} DTO.
   * <p>
   * Used heavily by the query/read layer to provide flattened data structures ready
   * for JSON serialization in API responses.
   *
   * @param e the JPA persistence entity to project
   * @return a flattened {@link UserView} DTO, or {@code null} if the input entity is null
   */
  public static UserView toView(UserEntity e) {
    if (e == null) {
      return null;
    }
    return new UserView(e.getId(), e.getCpf(), e.getName(), e.getCreatedAt(), e.getUpdatedAt());
  }
}