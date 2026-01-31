package com.pug.partner.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Entity entity aggregate. */
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

  @Builder(toBuilder = true)
  private Entity(UUID id, Cnpj cnpj, String name, UUID cityId, String address) {
    this.id = id;
    this.cnpj = cnpj;
    this.name = name;
    this.cityId = cityId;
    this.address = address;
  }

  /**
   * Factory for new entities.
   *
   * @param cnpj the CNPJ of the entity
   * @param name the name of the entity.
   * @param cityId the ID of the city where the entity is located
   * @param address the address where the entity is located
   * @return the created entity (may contain errors)
   */
  public static Entity factory(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity entity =
        Entity.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .cnpj(cnpj)
            .name(StringUtils.trim(name))
            .cityId(cityId)
            .address(StringUtils.trim(address))
            .build();

    entity.collectValidationProblems();
    return entity;
  }

  /**
   * Behavior: change the name of the entity.
   *
   * @param newName the new name of the entity
   * @return the updated entity with the new name
   */
  public Entity changeName(String newName) {
    String trimmed = StringUtils.trim(newName);
    if (this.name.equals(trimmed)) {
      return this;
    }
    Entity updated = this.toBuilder().name(trimmed).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: change the address where the entity is located.
   *
   * @param newAddress the new address of the entity
   * @return the updated entity with the new address
   */
  public Entity changeAddress(String newAddress) {
    String trimmed = StringUtils.trim(newAddress);
    if (this.address != null && this.address.equals(trimmed)) {
      return this;
    }
    Entity updated = this.toBuilder().address(trimmed).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: change the CNPJ of the entity.
   *
   * @param newCnpj the new CNPJ for the entity
   * @return the updated entity with the new CNPJ
   */
  public Entity changeCnpj(Cnpj newCnpj) {
    if (this.cnpj.equals(newCnpj)) {
      return this;
    }
    Entity updated = this.toBuilder().cnpj(newCnpj).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: move entity to another city.
   *
   * @param newCityId the new city ID where the entity will be located
   * @return the updated entity with the new city ID
   */
  public Entity moveToCity(UUID newCityId) {
    if (this.cityId.equals(newCityId)) {
      return this;
    }
    Entity updated = this.toBuilder().cityId(newCityId).build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Collects all validation problems for the entity's attributes. */
  private void collectValidationProblems() {
    if (id == null) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_ID_BLANK));
    }

    if (cnpj == null) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK));
    } else if (cnpj.hasErrors()) {
      addErrors(cnpj.getProblems());
    }

    if (StringUtils.isEmpty(name)) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_NAME_BLANK));
    } else if (name.length() > 150) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_NAME_LENGTH));
    }

    if (cityId == null) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CITY_BLANK));
    }

    if (address != null && address.length() > 254) {
      addError(new AppValidationException.Problem(PartnerErrorCodes.INVALID_ADDRESS_LENGTH));
    }
  }
}
