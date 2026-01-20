package com.pug.partner.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.utils.StringUtils;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity entity aggregate.
 */
@Getter
public class Entity {
  private final UUID id;
  private final Cnpj cnpj;
  private final String name;
  private final UUID cityId;
  private final String address;

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
   * @param cnpj    the CNPJ of the entity
   * @param name    the name of the entity.
   * @param cityId  the ID of the city where the entity is located
   * @param address the address where the entity is located
   * @return the created entity
   * @throws AppValidationException if initial validation fails.
   */
  public static Entity createNew(Cnpj cnpj, String name, UUID cityId, String address) {
    Entity entity =
            Entity.builder()
                    .id(UuidCreator.getTimeOrderedEpoch())
                    .cnpj(cnpj)
                    .name(StringUtils.trim(name))
                    .cityId(cityId)
                    .address(StringUtils.trim(address))
                    .build();

    List<AppValidationException.Problem> problems = entity.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return entity;
  }

  /**
   * Behavior: change the name of the entity.
   *
   * @param newName the new name of the entity
   * @return the updated entity with the new name
   * @throws AppValidationException if validation fails.
   */
  public Entity changeName(String newName) {
    Entity updatedEntity = this.toBuilder().name(StringUtils.trim(newName)).build();
    List<AppValidationException.Problem> problems = updatedEntity.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedEntity;
  }

  /**
   * Behavior: change the address where the entity is located.
   *
   * @param newAddress the new address of the entity
   * @return the updated entity with the new address
   * @throws AppValidationException if validation fails.
   */
  public Entity changeAddress(String newAddress) {
    Entity updatedEntity = this.toBuilder().address(StringUtils.trim(newAddress)).build();
    List<AppValidationException.Problem> problems = updatedEntity.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedEntity;
  }

  /**
   * Behavior: change the CNPJ of the entity.
   *
   * @param newCnpj the new CNPJ for the entity
   * @return the updated entity with the new CNPJ
   * @throws AppValidationException if validation fails.
   */
  public Entity changeCnpj(Cnpj newCnpj) {
    Entity updatedEntity = this.toBuilder().cnpj(newCnpj).build();
    List<AppValidationException.Problem> problems = updatedEntity.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedEntity;
  }

  /**
   * Behavior: move entity to another city.
   *
   * @param newCityId the new city ID where the entity will be located
   * @return the updated entity with the new city ID
   * @throws AppValidationException if validation fails.
   */
  public Entity moveToCity(UUID newCityId) {
    Entity updatedEntity = this.toBuilder().cityId(newCityId).build();
    List<AppValidationException.Problem> problems = updatedEntity.collectValidationProblems();
    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }
    return updatedEntity;
  }

  /**
   * Collects all validation problems for the entity's attributes.
   *
   * <p>Checks that ID, CNPJ is not null, name is not null or blank and does not exceed 150 characters,
   * cityId is not null, and address does not exceed 254 characters if provided.
   *
   * @return A list of {@code AppValidationException.Problem} if any validation fails; an empty list otherwise.
   */
  private List<AppValidationException.Problem> collectValidationProblems() {
    List<AppValidationException.Problem> problems = new ArrayList<>();

    if (id == null) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_ID_BLANK, "id"));
    }
    if (cnpj == null) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CNPJ_BLANK, "cnpj"));
    }
    if (StringUtils.isEmpty(name)) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_NAME_BLANK, "name"));
    } else if (name.length() > 150) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_NAME_LENGTH, "name"));
    }
    if (cityId == null) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_CITY_BLANK, "cityId"));
    }
    if (address != null && address.length() > 254) {
      problems.add(new AppValidationException.Problem(PartnerErrorCodes.INVALID_ADDRESS_LENGTH, "address"));
    }
    return problems;
  }
}