package br.org.catolicasc.pug.partner.infra;

import br.org.catolicasc.pug.identity.infra.persistence.AccountEntity;
import br.org.catolicasc.pug.identity.infra.read.dtos.AccountView;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.persistence.EntityEntity;
import br.org.catolicasc.pug.partner.infra.persistence.StaffEntity;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;

/** Stateless utility class responsible for mapping between Staff boundary layers. */
public final class StaffMapper {
  private StaffMapper() {}

  public static Staff toDomain(StaffEntity e) {
    if (e == null) {
      return null;
    }
    return Staff.builder().accountId(e.getAccountId()).entityId(e.getEntityId()).build();
  }

  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    return StaffEntity.builder().accountId(d.getAccountId()).entityId(d.getEntityId()).build();
  }

  /** Copies the mutable state of a staff aggregate into an attached persistence entity. */
  public static void copy(Staff d, StaffEntity e) {
    if (d == null || e == null) {
      return;
    }
    e.setEntityId(d.getEntityId());
  }

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
