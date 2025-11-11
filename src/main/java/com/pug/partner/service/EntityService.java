package com.pug.partner.service;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.StaffRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Service for managing partner entities. */
@ApplicationScoped
public class EntityService {

  @Inject EntityRepository repo;
  @Inject
  StaffService staffService;

  /**
   * Saves a new Entity.
   *
   * @param cnpj the CNPJ of the entity
   * @param name the name of the entity
   * @param cityId the city ID where the entity is located
   * @param address the address of the entity
   * @return the saved Entity
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   */
  @Transactional
  public Entity save(Cnpj cnpj, String name, UUID cityId, String address) {
    Objects.requireNonNull(cnpj, "cnpj");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(cityId, "cityId");

    String code = cnpj.toString();
    if (repo.existsByCnpj(code)) {
      throw new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS, Map.of("cnpj", cnpj.formatted()));
    }
    var e = Entity.createNew(cnpj, name, cityId, address);
    return repo.persist(e);
  }

  /**
   * Updates an existing Entity.
   *
   * @param id the UUID of the Entity to update
   * @param data the new data for the Entity
   * @throws ResourceNotFoundException if the Entity is not found
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   */
  @Transactional
  public void update(UUID id, Entity data) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(data, "data");
    var current = getById(id);

    if (!data.getCnpj().equals(current.getCnpj())
            && repo.existsByCnpj(data.getCnpj().toString())) {
      throw new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS, Map.of("cnpj", data.getCnpj().formatted()));
    }

    Entity updated =
        current
            .changeName(data.getName())
            .changeCnpj(data.getCnpj())
            .changeAddress(data.getAddress())
            .moveToCity(data.getCityId());
    repo.update(updated);
  }

  /**
   * Deletes Entities by their IDs.
   * Also deletes associated staff members and their underlying users.
   *
   * @param ids the UUIDs of the Entities to delete
   * @return a map containing the count of deleted entities, staff and users
   */
  @Transactional
  public Map<String, Long> deleteByIds(Iterable<UUID> ids) {
    if (ids == null || !ids.iterator().hasNext()) {
      return Map.of();
    }
    List<UUID> staffIds = toStream(ids).filter(Objects::nonNull).map(id->
            staffService.listByEntity(id))
            .flatMap(List::stream)
            .map(Staff::getUserId)
            .toList();
    var staff = staffService.deleteByUserIds(staffIds);
    var entities = repo.deleteByIds(ids);
    return Map.of(
            "entities", entities,
            "staff", staff.get("staff"),
            "users", staff.get("users"));
  }

  /**
   * Gets an Entity by its ID.
   *
   * @param id the UUID of the Entity
   * @return the Entity with the specified ID
   * @throws ResourceNotFoundException if the Entity is not found
   */
  public Entity getById(UUID id) {
    Objects.requireNonNull(id, "id");
    return repo.findOptionalById(id)
            .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));
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
   * Checks if any Entity exists in the given city IDs.
   *
   * @param cityIds the iterable of city UUIDs
   * @return true if any Entity exists in the specified cities, false otherwise
   */
  public boolean existsAnyByCityIdIn(Iterable<UUID> cityIds) {
    if (cityIds == null || !cityIds.iterator().hasNext()) {
      return false;
    }
    return repo.existsAnyByCityIdIn(cityIds);
  }

  /**
   * Converts an Iterable to a Stream.
   *
   * @param it The iterable to convert.
   * @param <T> The type of elements.
   * @return A stream of the iterable's elements.
   */
  private static <T> Stream<T> toStream(Iterable<T> it) {
    return (it == null) ? Stream.empty() : StreamSupport.stream(it.spliterator(), false);
  }
}
