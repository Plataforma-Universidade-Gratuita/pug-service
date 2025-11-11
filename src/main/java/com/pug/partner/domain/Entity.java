package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.text.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** Partner entity aggregate. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Entity {
  private final UUID id;
  private final Cnpj cnpj;
  private final String name;
  private final UUID cityId;
  private final String address;

  /**
   * Factory for new entities.
   *
   * @param cnpj the CNPJ.
   * @param name the name.
   * @param cityId the city ID.
   * @param address the address.
   * @return the created entity.
   */
  public static Entity createNew(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity e = new Entity(null, cnpj, StringUtils.trim(name), cityId, StringUtils.trim(address));
    e.validate();
    return e;
  }

  /**
   * Behavior: change name.
   *
   * @param newName the new name.
   * @return the updated entity.
   */
  public Entity changeName(String newName) {
    Entity e = this.toBuilder().name(StringUtils.trim(newName)).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: change address.
   *
   * @param newAddress the new address.
   * @return the updated entity.
   */
  public Entity changeAddress(String newAddress) {
    Entity e = this.toBuilder().address(StringUtils.trim(newAddress)).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: change CNPJ.
   *
   * @param newCnpj the new CNPJ.
   * @return the updated entity.
   */
  public Entity changeCnpj(Cnpj newCnpj) {
    Entity e = this.toBuilder().cnpj(newCnpj).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: move to another city.
   *
   * @param newCityId the new city ID.
   * @return the updated entity.
   */
  public Entity moveToCity(UUID newCityId) {
    Entity e = this.toBuilder().cityId(newCityId).build();
    e.validate();
    return e;
  }

  /**
   * Validates the entity's attributes.
   *
   * @throws AppValidationException if any attribute is invalid.
   */
  private void validate() {
    if (cnpj == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CNPJ);
    }
    if (name == null || name.isBlank()) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_NAME_BLANK);
    }
    if (name.length() > 150) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_NAME_TOOLONG);
    }
    if (cityId == null) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_CITY);
    }
    if (address != null && address.length() > 254) {
      throw new AppValidationException(PartnerErrorCodes.INVALID_ADDRESS_TOOLONG);
    }
  }

  /** Builder class for constructing Entity instances with validation. */
  public static class EntityBuilder {
    /**
     * Builds the Entity instance and performs validation.
     *
     * @return a validated Entity instance.
     * @throws AppValidationException if validation fails.
     */
    public Entity build() {
      Entity e = new Entity(id, cnpj, StringUtils.trim(name), cityId, StringUtils.trim(address));
      e.validate();
      return e;
    }
  }
}
