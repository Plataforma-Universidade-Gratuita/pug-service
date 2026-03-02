package com.pug.partner.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Entity entityId aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Entity extends DomainError {
  UUID id;
  Cnpj cnpj;
  String name;
  UUID cityId;
  String address;
  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private Entity(
      UUID id, Cnpj cnpj, String name, UUID cityId, String address, AuditInfo auditInfo) {
    this.id = id;
    this.cnpj = cnpj;
    this.name = name;
    this.cityId = cityId;
    this.address = address;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory for new entities.
   *
   * @param cnpj the CNPJ of the entityId
   * @param name the name of the entityId.
   * @param cityId the ID of the city where the entityId is located
   * @param address the address where the entityId is located
   * @return the created entityId (may contain errors)
   */
  public static Entity factory(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity entity =
        Entity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .cnpj(cnpj)
            .name(StringUtils.trim(name))
            .cityId(cityId)
            .address(StringUtils.trim(address))
            .auditInfo(AuditInfo.factory())
            .build();

    entity.collectValidationProblems();
    return entity;
  }

  /**
   * Behavior: change the name of the entityId.
   *
   * @param newName the new name of the entityId
   * @return the updated entityId with the new name
   */
  public Entity changeName(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (name.equals(trimmed)) {
      return this;
    }
    Entity updated = toBuilder().name(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: change the address where the entityId is located.
   *
   * @param newAddress the new address of the entityId
   * @return the updated entityId with the new address
   */
  public Entity changeAddress(String newAddress) {
    String trimmed = StringUtils.trim(newAddress);
    if (address != null && address.equals(trimmed)) {
      return this;
    }
    Entity updated = toBuilder().address(trimmed).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: change the CNPJ of the entityId.
   *
   * @param newCnpj the new CNPJ for the entityId
   * @return the updated entityId with the new CNPJ
   */
  public Entity changeCnpj(Cnpj newCnpj) {
    if (cnpj.equals(newCnpj)) {
      return this;
    }
    Entity updated = toBuilder().cnpj(newCnpj).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: move entityId to another city.
   *
   * @param newCityId the new city ID where the entityId will be located
   * @return the updated entityId with the new city ID
   */
  public Entity moveToCity(UUID newCityId) {
    if (cityId.equals(newCityId)) {
      return this;
    }
    Entity updated = toBuilder().cityId(newCityId).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Collects all validation problems for the entityId's attributes. */
  private void collectValidationProblems() {
    validateIdField(id);
    validateForeignKeyField(cityId, "cityId");
    validateStringField(name, 150L, "name");
    validateStringField(address, 254L, "address");
    if (cnpj == null) {
      addFieldError(new Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK));
    } else if (cnpj.hasFieldErrors()) {
      addFieldErrors(cnpj.getFieldErrors());
    }
    if (auditInfo == null) {
      addFieldError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else if (auditInfo.hasFieldErrors()) {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
