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
import com.pug.shared.exceptions.AppValidationException;

/**
 * Maps between Staff domain and StaffEntity persistence.
 */
public final class StaffMapper {
  /**
   * Private constructor to prevent instantiation.
   */
  private StaffMapper() {
  }

  /**
   * Maps a StaffEntity to a Staff domain object.
   *
   * @param e the StaffEntity.
   * @return the Staff domain object, or null if entity is null.
   * @throws AppValidationException if the data in the entity (e.g., accountId or entityId) is
   *                                invalid according to domain rules, indicating corrupted data in persistence.
   */
  public static Staff toDomain(StaffEntity e) throws AppValidationException {
    if (e == null) {
      return null;
    }
    return Staff.builder().accountId(e.getAccountId()).entityId(e.getEntityId()).build();
  }

  /**
   * Maps a Staff domain object to a StaffEntity for persistence.
   *
   * @param d the Staff domain object.
   * @return the StaffEntity, or null if domain is null.
   */
  public static StaffEntity toEntity(Staff d) {
    if (d == null) {
      return null;
    }
    return StaffEntity.builder().accountId(d.getAccountId()).entityId(d.getEntityId()).build();
  }

  /**
   * Converts an AccountEntity, EntityEntity, CityEntity, and UserEntity into a StaffView.
   *
   * @param accountEntity the associated AccountEntity.
   * @param entityEntity  the associated EntityEntity.
   * @param cityEntity    the associated CityEntity.
   * @param userEntity    the associated UserEntity.
   * @return the StaffView.
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
