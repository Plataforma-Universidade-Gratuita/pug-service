package com.pug.geo.service.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.ICityRepository;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.CityProcessor;
import com.pug.geo.service.ICityService;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
import com.pug.partner.service.IEntityService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service for managing cities. */
@ApplicationScoped
public class CityService implements ICityService {

  private static final Logger LOG = Logger.getLogger(CityService.class);

  @Inject ICityRepository repo;
  @Inject IEntityService entityService;

  @Transactional
  @Override
  public City save(CityCreateCommand cmd) {
    City cityToPersist = CityProcessor.processCreateInput(cmd.name(), cmd.ibgeCodeString());

    if (cityToPersist.hasErrors()) {
      throw new AppValidationException(cityToPersist.getProblems());
    }

    if (existsByIbge(cityToPersist.getIbgeCode())) {
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS,
          Map.of("code", cityToPersist.getIbgeCode().toString()));
    }

    return repo.persist(cityToPersist);
  }

  @Transactional
  @Override
  public List<City> saveAll(Iterable<CityCreateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<City> citiesToPersist = new ArrayList<>();
    Set<String> ibgeCodesInPayload = new HashSet<>();

    for (CityCreateCommand cmd : cmds) {
      City city = CityProcessor.processCreateInput(cmd.name(), cmd.ibgeCodeString());

      if (city.hasErrors()) {
        allCollectedProblems.addAll(city.getProblems());
      } else {
        String ibgeCodeStr = city.getIbgeCode().toString();

        if (!ibgeCodesInPayload.add(ibgeCodeStr)) {
          allCollectedProblems.add(
              new AppValidationException.Problem(GeoErrorCodes.CITY_ALREADY_EXISTS));
        }
        citiesToPersist.add(city);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> ibgeCodesToPersist =
        citiesToPersist.stream().map(c -> c.getIbgeCode().toString()).toList();

    if (repo.existsAnyByIbgeCodeIn(ibgeCodesToPersist)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }

    return repo.persistAll(citiesToPersist);
  }

  @Transactional
  @Override
  public City update(UUID id, CityUpdateCommand cmd) {
    City current = getById(id);

    City updated = CityProcessor.processUpdateInput(current, cmd.name(), cmd.ibgeCodeString());

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getIbgeCode().equals(current.getIbgeCode())
        && existsByIbge(updated.getIbgeCode())) {
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", updated.getIbgeCode().toString()));
    }

    repo.update(updated);

    return getById(id);
  }

  @Transactional
  @Override
  public Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Map.of(DeleteKeys.CITIES, 0L);
    }

    if (entityService.existsAnyByCityIdIn(ids)) {
      throw new ReferencedEntityException(GeoErrorCodes.CITY_STILL_REFERENCED_BY_ENTITY);
    }
    return Map.of(DeleteKeys.CITIES, repo.deleteByIds(ids));
  }

  @Override
  public City getById(UUID id) {
    City city =
        repo.findOptionalById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id)));

    if (city.hasErrors()) {
      LOG.errorf(
          "Data integrity error: City with ID %s in DB violates domain rules. Problems: %s",
          id, city.getProblemsSummary());
      throw new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id));
    }

    return city;
  }

  @Override
  public City getByIbge(String ibgeCodeString) {
    City city =
        repo.findOptionalByIbgeCode(ibgeCodeString)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        GeoErrorCodes.CITY_NOT_FOUND, Map.of("code", ibgeCodeString)));

    if (city.hasErrors()) {
      LOG.errorf(
          "Data integrity error: City with IBGE code %s in DB violates domain rules. Problems: %s",
          ibgeCodeString, city.getProblemsSummary());
      throw new ResourceNotFoundException(
          GeoErrorCodes.CITY_NOT_FOUND, Map.of("code", ibgeCodeString));
    }

    return city;
  }

  @Override
  public boolean existsByIbge(IbgeCode ibgeCode) {
    if (ibgeCode == null) {
      return false;
    }
    return repo.existsByIbgeCode(ibgeCode.toString());
  }
}
