package com.pug.partner.infra;

import com.pug.identity.infra.persistence.AccountEntity;
import com.pug.identity.infra.read.dtos.AccountView;
import com.pug.partner.domain.Staff;
import com.pug.partner.infra.persistence.EntityEntity;
import com.pug.partner.infra.persistence.StaffEntity;
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
   * Projects a deeply nested set of JPA entities across multiple domains into a flattened {@link
   * StaffView} DTO.
   *
   * <p>Used heavily by the CQRS query layer to construct fully resolved data structures that
   * encapsulate the staff member's account and organizational linkage in a single response,
   * exposing only the identifiers of the partner entity and city so that additional details can be
   * resolved on demand.
   *
   * @param accountEntity the JPA entity representing the linked authentication account
   * @param entityEntity the JPA entity representing the partner organization
   * @return a fully populated {@link StaffView} DTO
   */
  public static StaffView toView(AccountEntity accountEntity, EntityEntity entityEntity) {

    var accountView =
        new AccountView(
            accountEntity.getId(),
            accountEntity.getUserId(),
            accountEntity.getEmail(),
            accountEntity.getAccountType(),
            accountEntity.getCreatedAt(),
            accountEntity.getUpdatedAt(),
            accountEntity.getActive());

    return new StaffView(accountView, entityEntity.getId(), entityEntity.getCityId());
  }
}
