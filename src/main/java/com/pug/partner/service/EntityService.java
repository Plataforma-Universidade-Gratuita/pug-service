package com.pug.partner.service;

import com.pug.geo.service.CityService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.dtos.EntityCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Service for managing partner entities. */
@ApplicationScoped
public class EntityService {

  @Inject EntityRepository repo;
  @Inject CityService cityService;
  @Inject StaffService staffService;

  /**
   * Saves a new Entity.
   *
   * @param cmd the command containing the data to create the Entity
   * @return the saved Entity
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   */
  @Transactional
  public Entity save(EntityCreateOrUpdateCommand cmd) {
    if (existsByCnpj(cmd.cnpj())) {
      throw new DuplicateResourceException(
          PartnerErrorCodes.ENTITY_ALREADY_EXISTS, Map.of("cnpj", cmd.cnpj()));
    }
    var city = cityService.getByIbge(cmd.cityIbge());
    var e = Entity.createNew(cmd.cnpj(), cmd.name(), city.getId(), cmd.address());
    return repo.persist(e);
  }

  /**
   * Updates an existing Entity.
   *
   * @param id the UUID of the Entity to update
   * @param cmd the command containing the updated data for the Entity
   * @throws ResourceNotFoundException if the Entity is not found
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   */
  @Transactional
  public Entity update(UUID id, EntityCreateOrUpdateCommand cmd) {
    var current = getById(id);

    Cnpj cnpj;
    if (cmd.cnpj() != null) {
      if (!cmd.cnpj().equals(current.getCnpj()) && existsByCnpj(cmd.cnpj())) {
        throw new DuplicateResourceException(
            PartnerErrorCodes.ENTITY_ALREADY_EXISTS, Map.of("cnpj", cmd.cnpj()));
      }
      cnpj = cmd.cnpj();
    } else {
      cnpj = current.getCnpj();
    }
    var cityId =
        cmd.cityIbge() != null
            ? cityService.getByIbge(cmd.cityIbge()).getId()
            : current.getCityId();
    var name = cmd.name() != null ? cmd.name() : current.getName();
    var address = cmd.address() != null ? cmd.address() : current.getAddress();

    Entity updated =
        current.changeName(name).changeCnpj(cnpj).changeAddress(address).moveToCity(cityId);
    repo.update(updated);
    return getById(id);
  }

  /**
   * Deletes Entities by their IDs.
   *
   * <p>Also deletes associated staff members and their underlying users.
   *
   * @param ids the UUIDs of the Entities to delete
   * @return a map containing the count of deleted entities, staff and accounts
   */
  @Transactional
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of();
    }
    List<UUID> staffIds =
        CollectionUtils.toStream(ids)
            .filter(Objects::nonNull)
            .map(id -> staffService.listByEntity(id))
            .flatMap(List::stream)
            .map(Staff::getAccountId)
            .toList();
    var staff = staffService.deleteAll(staffIds);
    var entities = repo.deleteByIds(ids);
    return Map.of(
        DeleteKeys.ENTITIES, entities,
        DeleteKeys.STAFF, staff.getOrDefault(DeleteKeys.STAFF, 0L),
        DeleteKeys.ACCOUNTS, staff.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, staff.getOrDefault(DeleteKeys.USERS, 0L));
  }

  /**
   * Gets an Entity by its ID.
   *
   * @param id the UUID of the Entity
   * @return the Entity with the specified ID
   * @throws ResourceNotFoundException if the Entity is not found
   */
  public Entity getById(UUID id) {
    return repo.findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Gets an Entity by its CNPJ.
   *
   * @param cnpj the CNPJ of the Entity
   * @return the Entity with the specified CNPJ
   * @throws ResourceNotFoundException if the Entity is not found
   */
  public Entity getByCnpj(Cnpj cnpj) {
    return repo.findOptionalByCnpj(cnpj.toString())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj)));
  }

  /**
   * Lists all Entities.
   *
   * @return a list of all Entities
   */
  public List<Entity> listAll() {
    return repo.listAllEntities();
  }

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check
   * @return true if an Entity with the given CNPJ exists, false otherwise
   */
  public boolean existsByCnpj(Cnpj cnpj) {
    if (cnpj == null) {
      return false;
    }
    return repo.existsByCnpj(cnpj.toString());
  }

  /**
   * Checks if any Entity exists in the given city IDs.
   *
   * @param cityIds the iterable of city UUIDs
   * @return true if any Entity exists in the specified cities, false otherwise
   */
  public boolean existsAnyByCityIdIn(Iterable<UUID> cityIds) {
    if (CollectionUtils.isEmpty(cityIds)) {
      return false;
    }
    return repo.existsAnyByCityIdIn(cityIds);
  }
}
