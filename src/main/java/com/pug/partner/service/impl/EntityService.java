package com.pug.partner.service.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.ICityService;
import com.pug.partner.domain.Entity;
import com.pug.partner.domain.IEntityRepository;
import com.pug.partner.domain.Staff;
import com.pug.partner.domain.enums.PartnerErrorCodes;
import com.pug.partner.domain.vos.Cnpj;
import com.pug.partner.service.IEntityService;
import com.pug.partner.service.IStaffService;
import com.pug.partner.service.dtos.EntityCreateOrUpdateCommand;
import com.pug.shared.domain.enums.DeleteKeys;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ReferencedEntityException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

/** Service for managing partner entities. */
@ApplicationScoped
public class EntityService implements IEntityService {

  private static final Logger LOG = Logger.getLogger(EntityService.class);

  @Inject IEntityRepository repo;
  @Inject ICityService cityService;
  @Inject IStaffService staffService;

  /**
   * Helper method to process DTO input and build Entity domain object (or update existing),
   * collecting all validation problems.
   *
   * @param cnpjString The CNPJ string from DTO.
   * @param name The name string from DTO.
   * @param cityIbgeString The city IBGE code string from DTO.
   * @param address The address string from DTO.
   * @param existingEntity Optional existing entity for updates (null for creation).
   * @param problems List to collect AppValidationException.Problem instances.
   * @return The constructed or updated Entity domain object if no problems, or null if problems
   *     occurred.
   */
  private Entity processEntityInput(
      String cnpjString,
      String name,
      String cityIbgeString,
      String address,
      Entity existingEntity,
      List<AppValidationException.Problem> problems) {

    Cnpj cnpjVO = null;
    try {
      if (cnpjString != null && !cnpjString.isBlank()) {
        cnpjVO = new Cnpj(cnpjString);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    UUID cityId = null;
    IbgeCode cityIbgeVO = null;
    try {
      if (cityIbgeString != null && !cityIbgeString.isBlank()) {
        cityIbgeVO = new IbgeCode(cityIbgeString);
        City city = cityService.getByIbge(cityIbgeVO);
        cityId = city.getId();
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    } catch (ResourceNotFoundException e) {
      problems.add(
          new AppValidationException.Problem(
              PartnerErrorCodes.INVALID_CITY_BLANK, "cityIbgeString"));
    }

    Entity resultEntity = null;
    try {
      if (existingEntity == null) {
        resultEntity = Entity.createNew(cnpjVO, name, cityId, address);
      } else {
        Cnpj effectiveCnpj = (cnpjVO != null) ? cnpjVO : existingEntity.getCnpj();
        String effectiveName = (name != null) ? name : existingEntity.getName();
        UUID effectiveCityId = (cityId != null) ? cityId : existingEntity.getCityId();
        String effectiveAddress = (address != null) ? address : existingEntity.getAddress();

        Entity tempEntity = existingEntity;
        if (cnpjVO != null && !effectiveCnpj.equals(tempEntity.getCnpj())) {
          tempEntity = tempEntity.changeCnpj(effectiveCnpj);
        }
        if (name != null && !effectiveName.equals(tempEntity.getName())) {
          tempEntity = tempEntity.changeName(effectiveName);
        }
        if (cityId != null && !effectiveCityId.equals(tempEntity.getCityId())) {
          tempEntity = tempEntity.moveToCity(effectiveCityId);
        }
        if (address != null && !effectiveAddress.equals(tempEntity.getAddress())) {
          tempEntity = tempEntity.changeAddress(effectiveAddress);
        }
        resultEntity = tempEntity;
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultEntity;
  }

  @Transactional
  @Override
  public Entity save(EntityCreateOrUpdateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    Entity entityToPersist =
        processEntityInput(
            cmd.cnpjString(), cmd.name(), cmd.cityIbgeString(), cmd.address(), null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByCnpj(entityToPersist.getCnpj())) {
      throw new DuplicateResourceException(
          PartnerErrorCodes.ENTITY_ALREADY_EXISTS,
          Map.of("cnpj", entityToPersist.getCnpj().toString()));
    }
    return repo.persist(entityToPersist);
  }

  @Transactional
  @Override
  public Entity update(UUID id, EntityCreateOrUpdateCommand cmd) {
    Entity current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();

    Entity entityToUpdate =
        processEntityInput(
            cmd.cnpjString(), cmd.name(), cmd.cityIbgeString(), cmd.address(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (cmd.cnpjString() != null) {
      try {
        Cnpj newCnpjVO = new Cnpj(cmd.cnpjString());
        if (!newCnpjVO.equals(current.getCnpj()) && existsByCnpj(newCnpjVO)) {
          throw new DuplicateResourceException(
              PartnerErrorCodes.ENTITY_ALREADY_EXISTS, Map.of("cnpj", newCnpjVO.toString()));
        }
      } catch (AppValidationException e) {
        problems.addAll(e.getProblems());
        throw new AppValidationException(problems);
      }
    }

    repo.update(entityToUpdate);
    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of();
    }

    List<UUID> staffAccountIdsToDelete = new ArrayList<>();
    for (UUID entityId : ids) {
      if (staffService.listByEntity(entityId).stream()
          .anyMatch(staff -> Objects.nonNull(staff.getAccountId()))) {
        throw new ReferencedEntityException(
            PartnerErrorCodes.ENTITY_STILL_REFERENCED, Map.of("entityId", entityId));
      }
    }

    for (UUID entityId : ids) {
      staffService.listByEntity(entityId).stream()
          .map(Staff::getAccountId)
          .filter(Objects::nonNull)
          .forEach(staffAccountIdsToDelete::add);
    }

    Map<DeleteKeys, Long> deletedStaffAndAccounts = Map.of();
    if (!staffAccountIdsToDelete.isEmpty()) {
      deletedStaffAndAccounts = staffService.deleteAll(staffAccountIdsToDelete);
    }

    long entitiesDeleted = repo.deleteByIds(ids);

    return Map.of(
        DeleteKeys.ENTITIES, entitiesDeleted,
        DeleteKeys.STAFF, deletedStaffAndAccounts.getOrDefault(DeleteKeys.STAFF, 0L),
        DeleteKeys.ACCOUNTS, deletedStaffAndAccounts.getOrDefault(DeleteKeys.ACCOUNTS, 0L),
        DeleteKeys.USERS, deletedStaffAndAccounts.getOrDefault(DeleteKeys.USERS, 0L));
  }

  @Override
  public Entity getById(UUID id) {
    try {
      return repo.findOptionalById(id)
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Entity with ID %s in DB violates domain rules. Problems: %s",
          id,
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("id", id));
    }
  }

  @Override
  public Entity getByCnpj(Cnpj cnpj) {
    try {
      return repo.findOptionalByCnpj(cnpj.toString())
          .orElseThrow(
              () ->
                  new ResourceNotFoundException(
                      PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj.toString())));
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Entity with CNPJ %s in DB violates domain rules. Problems: %s",
          cnpj.toString(),
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(
          PartnerErrorCodes.ENTITY_NOT_FOUND, Map.of("cnpj", cnpj.toString()));
    }
  }

  @Override
  public List<Entity> listAll() {
    try {
      return repo.listAllEntities();
    } catch (AppValidationException e) {
      LOG.errorf(
          e,
          "Data integrity error: Corrupted Entity entity found in DB. Problems: %s",
          e.getProblems().stream()
              .map(
                  p ->
                      p.code().getBundleKey()
                          + (p.fieldName() != null ? "(" + p.fieldName() + ")" : ""))
              .collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(PartnerErrorCodes.ENTITY_NOT_FOUND);
    }
  }

  @Override
  public boolean existsByCnpj(Cnpj cnpj) {
    if (cnpj == null) {
      return false;
    }
    return repo.existsByCnpj(cnpj.toString());
  }

  @Override
  public boolean existsAnyByCityIdIn(Iterable<UUID> cityIds) {
    if (CollectionUtils.isEmpty(cityIds)) {
      return false;
    }
    return repo.existsAnyByCityIdIn(cityIds);
  }
}
