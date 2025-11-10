package com.pug.partner.service;

import com.pug.partner.domain.Entity;
import com.pug.partner.domain.EntityRepository;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.text.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Service for managing partner entities. */
@ApplicationScoped
public class EntityService {

  @Inject EntityRepository repo;

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
      throw new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
    }
    var e = Entity.createNew(cnpj, name, cityId, address);
    return repo.persist(e);
  }

  /**
   * Saves a new Entity.
   *
   * @param entity the Entity to save
   * @return the saved Entity
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists
   */
  @Transactional
  public Entity save(Entity entity) {
    Objects.requireNonNull(entity, "entity");
    String code = entity.getCnpj().toString();
    if (repo.existsByCnpj(code)) {
      throw new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
    }
    return repo.persist(entity);
  }

  /**
   * Saves multiple Entities.
   *
   * @param entities the iterable collection of Entities to save
   * @return a list of the saved Entities
   * @throws DuplicateResourceException if any entity with the same CNPJ already exists
   */
  @Transactional
  public List<Entity> saveAll(Iterable<Entity> entities) {
    Objects.requireNonNull(entities, "entities");
    List<Entity> list = new ArrayList<>();
    for (Entity e : entities) {
      if (e != null) {
        String code = e.getCnpj().toString();
        if (repo.existsByCnpj(code)) {
          throw new DuplicateResourceException(PartnerErrorCodes.ENTITY_ALREADY_EXISTS);
        }
        list.add(e);
      }
    }
    return repo.persistAll(list);
  }

  /**
   * Deletes Entities by their IDs.
   *
   * @param ids the iterable collection of UUIDs representing the IDs of the Entities to delete
   * @return the number of entities deleted
   */
  @Transactional
  public long deleteByIds(Iterable<UUID> ids) {
    Objects.requireNonNull(ids, "ids");
    return repo.deleteByIds(ids);
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
   * Lists all Entities by city ID.
   *
   * @param cityId the ID of the city
   * @return a list of Entities located in the specified city
   */
  public List<Entity> listAllByCityId(UUID cityId) {
    Objects.requireNonNull(cityId, "cityId");
    return repo.listAllByCityId(cityId);
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
        .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND));
  }

  /**
   * Gets an Entity by its CNPJ.
   *
   * @param cnpj the CNPJ of the Entity
   * @return the Entity with the specified CNPJ
   * @throws ResourceNotFoundException if the Entity is not found
   */
  public Entity getByCnpj(Cnpj cnpj) {
    Objects.requireNonNull(cnpj, "cnpj");
    return repo.findOptionalByCnpj(cnpj)
        .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND));
  }

  /**
   * Searches for Entities by name.
   *
   * @param query the search query for the entity name
   * @return a list of Entities matching the given name
   */
  public List<Entity> search(String query) {
    Objects.requireNonNull(query, "query");
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key);
  }

  /**
   * Checks if an Entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ of the Entity
   * @return true if the Entity exists, false otherwise
   */
  public boolean existsByCnpj(String cnpj) {
    Objects.requireNonNull(cnpj, "cnpj");
    return repo.existsByCnpj(cnpj);
  }
}
