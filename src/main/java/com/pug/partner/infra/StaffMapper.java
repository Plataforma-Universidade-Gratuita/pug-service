package com.pug.partner.infra;

import com.pug.geo.infra.persistence.CityEntity;
import com.pug.geo.infra.read.dtos.CityView;
import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.persistence.UserEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.identity.infra.read.dtos.UserView;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.persistence.StaffEntity;
import com.pug.partner.infra.read.dtos.EntityView;
import com.pug.partner.infra.read.dtos.StaffView;

/**
 * Stateless utility class responsible for mapping between Staff boundary layers.
 *
 * <p>This mapper handles both the translation between pure domain and JPA entities, as well as the
 * assembly of deeply nested CQRS read views that cross module boundaries (Identity, Geo, Partner).
 */
public final class StaffMapper {
  /** Private constructor to prevent instantiation. */
  private StaffMapper() {}

  /**
   * Reconstitutes a pure Domain {@link Staff} aggregate from a JPA {@link StaffEntity}.
   *
   * @param e the JPA persistence entity to convert
   * @return a fully constructed Domain {@link Staff}, or {@code null} if the input entity is null
   */
  public static Staff toDomain(StaffEntity e) {
    if (e == null) {
      return null;
    }
    return Staff.builder().accountId(e.getAccountId()).entityId(e.getEntityId()).build();
  }

  /**
   * Translates a pure Domain {@link Staff} aggregate into a newly instantiated JPA {@link
   * StaffEntity}.
   *
   * @param d the Domain aggregate to convert
   * @return a newly constructed JPA {@link StaffEntity}, or {@code null} if the input domain is
   *     null
   */
  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    return StaffEntity.builder().accountId(d.getAccountId()).entityId(d.getEntityId()).build();
  }

  /**
   * Projects a deeply nested set of JPA Entities across multiple domains into a comprehensive
   * {@link StaffView} DTO.
   *
   * <p>Used heavily by the CQRS query layer to construct fully resolved data structures that
   * encapsulate the staff member's profile, credentials, organization, and location in a single,
   * flattened response ready for JSON serialization.
   *
   * @param accountEntity the JPA entity representing the linked authentication account
   * @param entityEntity the JPA entity representing the partner organization
   * @param cityEntity the JPA entity representing the city where the partner operates
   * @param userEntity the JPA entity representing the personal identity of the staff member
   * @return a fully populated {@link StaffView} DTO
   */
  public static StaffView toView(
      AccountEntity accountEntity,
      EntityEntity entityEntity,
      CityEntity cityEntity,
      UserEntity userEntity) {
    return new StaffView(
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
        new EntityView(
            entityEntity.getId(),
            entityEntity.getCnpj(),
            entityEntity.getName(),
            entityEntity.getAddress(),
            new CityView(cityEntity.getId(), cityEntity.getName(), cityEntity.getIbgeCode()),
            entityEntity.getCreatedAt(),
            entityEntity.getUpdatedAt()));
  }
}
