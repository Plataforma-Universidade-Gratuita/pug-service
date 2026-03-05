package com.pug.partner.service.utils;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into pure {@link Entity}
 * Domain Aggregates and Value Objects.
 *
 * <p>This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the application
 * service layer.
 */
public class EntityProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Entity} domain aggregate.
   *
   * <p>This method translates the raw string representations into appropriate Value Objects (e.g.,
   * {@link Cnpj}) before passing them to the entity's factory method.
   *
   * <p><b>Note:</b> The returned {@link Entity} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link Entity#hasFieldErrors()} and handling
   * them appropriately.
   *
   * @param cnpjString the raw 14-digit CNPJ string requested for creation
   * @param name the raw name of the partner organization
   * @param cityId the unique identifier of the associated city
   * @param address the physical street address
   * @return a fully instantiated {@link Entity} domain aggregate, potentially containing validation
   *     errors
   */
  public static Entity processCreateInput(
      String cnpjString, String name, UUID cityId, String address) {

    Cnpj cnpjVo = Cnpj.factory(cnpjString);

    return Entity.factory(cnpjVo, name, cityId, address);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Entity}.
   *
   * <p>This method applies partial updates. Only fields that are explicitly provided (i.e., not
   * null and not empty) will trigger a state mutation via the aggregate's domain behaviors.
   *
   * <p>Because domain entities in this system are modeled as immutable records, this method returns
   * a <i>new</i> instance of the {@link Entity} reflecting the applied changes.
   *
   * @param existingEntity the current, reconstituted {@link Entity} aggregate from the repository
   * @param cnpjString the proposed new CNPJ string, or {@code null}/empty to skip updating
   * @param name the proposed new name, or {@code null}/empty to skip updating
   * @param cityId the proposed new city ID, or {@code null} to skip updating
   * @param address the proposed new address, or {@code null}/empty to skip updating
   * @return a new {@link Entity} domain aggregate reflecting the requested updates, potentially
   *     containing validation errors
   */
  public static Entity processUpdateInput(
      Entity existingEntity, String cnpjString, String name, UUID cityId, String address) {

    Entity updatedEntity = existingEntity;

    if (StringUtils.isNotEmpty(cnpjString)) {
      Cnpj newCnpj = Cnpj.factory(cnpjString);
      updatedEntity = updatedEntity.changeCnpj(newCnpj);
    }

    if (StringUtils.isNotEmpty(name)) {
      updatedEntity = updatedEntity.rename(name);
    }

    if (cityId != null) {
      updatedEntity = updatedEntity.moveToCity(cityId);
    }

    if (StringUtils.isNotEmpty(address)) {
      updatedEntity = updatedEntity.moveToAddress(address);
    }

    return updatedEntity;
  }
}
