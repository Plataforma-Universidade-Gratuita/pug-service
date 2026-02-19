package com.pug.partner.service.utils;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;

/** Utility class for processing Entity DTO inputs. */
public class EntityProcessor {

  /**
   * Helper method to process DTO input and build a new Entity domain object.
   *
   * @param cnpjString The CNPJ string from DTO.
   * @param name The entity name from DTO.
   * @param cityId The resolved city ID.
   * @param address The address from DTO.
   * @return The constructed Entity domain object (may contain validation errors).
   */
  public static Entity processCreateInput(
      String cnpjString, String name, UUID cityId, String address) {

    Cnpj cnpjVo = Cnpj.factory(cnpjString);

    return Entity.factory(cnpjVo, name, cityId, address);
  }

  /**
   * Helper method to process DTO input and update an existing Entity domain object.
   *
   * @param existingEntity The existing entity to be updated.
   * @param cnpjString The CNPJ string from DTO (can be null for no change).
   * @param name The name from DTO (can be null for no change).
   * @param cityId The resolved city ID (can be null for no change).
   * @param address The address from DTO (can be null for no change).
   * @return The updated Entity domain object (may contain validation errors).
   */
  public static Entity processUpdateInput(
      Entity existingEntity, String cnpjString, String name, UUID cityId, String address) {

    Entity updatedEntity = existingEntity;

    if (StringUtils.isNotEmpty(cnpjString)) {
      Cnpj newCnpj = Cnpj.factory(cnpjString);
      updatedEntity = updatedEntity.changeCnpj(newCnpj);
    }

    if (StringUtils.isNotEmpty(name)) {
      updatedEntity = updatedEntity.changeName(name);
    }

    if (cityId != null) {
      updatedEntity = updatedEntity.moveToCity(cityId);
    }

    if (StringUtils.isNotEmpty(address)) {
      updatedEntity = updatedEntity.changeAddress(address);
    }

    return updatedEntity;
  }
}
