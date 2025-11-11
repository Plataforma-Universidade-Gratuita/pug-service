package com.pug.partner.domain;

import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Entity entity aggregate.
 */
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
   * @param cnpj the CNPJ of the entity
   * @param name the name of the entity.
   * @param cityId the ID of the city where the entity is located
   * @param address the address where the entity is located
   * @return the created entity
   */
  public static Entity createNew(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity e = new Entity(null, cnpj, StringUtils.trim(name), cityId, StringUtils.trim(address));
    e.validate();
    return e;
  }

  /**
   * Behavior: change the name of the entity.
   *
   * @param newName the new name of the entity
   * @return the updated entity with the new name
   */
  public Entity changeName(String newName) {
    Entity e = this.toBuilder().name(StringUtils.trim(newName)).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: change the address where the entity is located.
   *
   * @param newAddress the new address of the entity
   * @return the updated entity with the new address
   */
  public Entity changeAddress(String newAddress) {
    Entity e = this.toBuilder().address(StringUtils.trim(newAddress)).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: change the CNPJ of the entity.
   *
   * @param newCnpj the new CNPJ for the entity
   * @return the updated entity with the new CNPJ
   */
  public Entity changeCnpj(Cnpj newCnpj) {
    Entity e = this.toBuilder().cnpj(newCnpj).build();
    e.validate();
    return e;
  }

  /**
   * Behavior: move entity to another city.
   *
   * @param newCityId the new city ID where the entity will be located
   * @return the updated entity with the new city ID
   */
  public Entity moveToCity(UUID newCityId) {
    Entity e = this.toBuilder().cityId(newCityId).build();
    e.validate();
    return e;
  }

  /**
   * Validates the entity's attributes.
   *
   * <p>Checks that CNPJ is not null, name is not null or blank and does not exceed 150 characters,
   * cityId is not null, and address does not exceed 254 characters if provided.</
   *
   * @throws AppValidationException if any attribute is invalid
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

  /**
   * Builder class for Entity.
   * <p>Overrides the build method to include validation.</p>
   */
  public static class EntityBuilder {
    /**
     * Builds the Entity instance and performs validation.
     *
     * @return a validated Entity instance
     */
    public Entity build() {
      Entity e = new Entity(id, cnpj, StringUtils.trim(name), cityId, StringUtils.trim(address));
      e.validate();
      return e;
    }
  }
}
