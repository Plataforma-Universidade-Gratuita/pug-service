package com.pug.partner.service;

import com.pug.partner.domain.EntitiesRepository;
import com.pug.partner.domain.Entity;
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
public class EntitiesService {

  @Inject EntitiesRepository repo;

  /**
   * Saves a new entity after checking for duplicates by CNPJ.
   *
   * @param entity the entity to be saved.
   * @return the saved entity.
   * @throws DuplicateResourceException if an entity with the same CNPJ already exists.
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
   * Saves multiple entities after checking for duplicates by CNPJ.
   *
   * @param entities the entities to be saved.
   * @return the list of saved entities.
   * @throws DuplicateResourceException if any entity with the same CNPJ already exists.
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
   * Deletes entities by their IDs.
   *
   * @param ids the IDs of the entities to be deleted.
   * @return the number of entities deleted.
   */
  @Transactional
  public long deleteByIds(Iterable<UUID> ids) {
    Objects.requireNonNull(ids, "ids");
    return repo.deleteByIds(ids);
  }

  /**
   * Lists all entities.
   *
   * @return a list of all entities.
   */
  public List<Entity> listAll() {
    return repo.listAllEntities();
  }

  /**
   * Lists all entities by city ID.
   *
   * @param cityId the ID of the city.
   * @return a list of entities in the specified city.
   */
  public List<Entity> listAllByCityId(UUID cityId) {
    Objects.requireNonNull(cityId, "cityId");
    return repo.listAllByCityId(cityId);
  }

  /**
   * Retrieves an entity by its ID.
   *
   * @param id the ID of the entity.
   * @return the entity with the specified ID.
   * @throws ResourceNotFoundException if no entity with the specified ID is found.
   */
  public Entity getById(UUID id) {
    Objects.requireNonNull(id, "id");
    return repo.findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND));
  }

  /**
   * Retrieves an entity by its CNPJ.
   *
   * @param cnpj the CNPJ of the entity.
   * @return the entity with the specified CNPJ.
   * @throws ResourceNotFoundException if no entity with the specified CNPJ is found.
   */
  public Entity getByCnpj(Cnpj cnpj) {
    Objects.requireNonNull(cnpj, "cnpj");
    return repo.findOptionalByCnpj(cnpj)
        .orElseThrow(() -> new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND));
  }

  /**
   * Searches for entities by name.
   *
   * @param query the search query.
   * @return a list of entities matching the search query.
   */
  public List<Entity> search(String query) {
    Objects.requireNonNull(query, "query");
    String key = StringUtils.fold(query).toLowerCase(Locale.ROOT);
    return repo.searchByName(key);
  }

  /**
   * Checks if an entity exists by its CNPJ.
   *
   * @param cnpj the CNPJ to check.
   * @return true if an entity with the specified CNPJ exists, false otherwise.
   */
  public boolean existsByCnpj(String cnpj) {
    Objects.requireNonNull(cnpj, "cnpj");
    return repo.existsByCnpj(cnpj);
  }
}
