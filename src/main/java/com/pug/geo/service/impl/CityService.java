package com.pug.geo.service.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.ICityRepository;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.ICityService;
import com.pug.geo.service.dtos.CityCreateOrUpdateCommand;
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
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing cities.
 */
@ApplicationScoped
public class CityService implements ICityService {

  private static final Logger LOG = Logger.getLogger(CityService.class);

  @Inject
  ICityRepository repo;
  @Inject
  IEntityService entityService;

  /**
   * Helper method to process DTO input and build City domain object,
   * collecting all validation problems.
   *
   * @param name           The city name from DTO.
   * @param ibgeCodeString The IBGE code string from DTO.
   * @param existingCity   Optional existing city for updates (null for creation).
   * @param problems       List to collect AppValidationException.Problem instances.
   * @return The constructed or updated City domain object if no problems, or null if problems occurred.
   */
  private City processCityInput(String name, String ibgeCodeString, City existingCity, List<AppValidationException.Problem> problems) {
    IbgeCode ibgeCodeVO = null;
    try {
      if (ibgeCodeString != null && !ibgeCodeString.isBlank()) {
        ibgeCodeVO = new IbgeCode(ibgeCodeString);
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }

    City resultCity = null;
    try {
      if (existingCity == null) {
        resultCity = City.createNew(name, ibgeCodeVO);
      } else {
        String effectiveName = (name != null) ? name : existingCity.getName();
        IbgeCode effectiveIbgeCode = (ibgeCodeVO != null) ? ibgeCodeVO : existingCity.getIbgeCode();

        City tempCity = existingCity;
        if (name != null && !effectiveName.equals(tempCity.getName())) {
          tempCity = tempCity.changeName(effectiveName);
        }
        if (ibgeCodeVO != null && !effectiveIbgeCode.equals(tempCity.getIbgeCode())) {
          tempCity = tempCity.changeIbgeCode(effectiveIbgeCode);
        }
        resultCity = tempCity;
      }
    } catch (AppValidationException e) {
      problems.addAll(e.getProblems());
    }
    return resultCity;
  }

  @Transactional
  @Override
  public City save(CityCreateOrUpdateCommand cmd) {
    List<AppValidationException.Problem> problems = new ArrayList<>();
    City cityToPersist = processCityInput(cmd.name(), cmd.ibgeCodeString(), null, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (existsByIbge(cityToPersist.getIbgeCode())) {
      throw new DuplicateResourceException(
              GeoErrorCodes.CITY_ALREADY_EXISTS, Map.of("code", cityToPersist.getIbgeCode().toString()));
    }
    return repo.persist(cityToPersist);
  }

  @Transactional
  @Override
  public List<City> saveAll(Iterable<CityCreateOrUpdateCommand> cmds) {
    if (CollectionUtils.isEmpty(cmds)) {
      return List.of();
    }

    List<AppValidationException.Problem> allCollectedProblems = new ArrayList<>();
    List<City> citiesToPersist = new ArrayList<>();
    Set<String> ibgeCodesInPayload = new HashSet<>();

    for (CityCreateOrUpdateCommand cmd : cmds) {
      List<AppValidationException.Problem> currentCityProblems = new ArrayList<>();
      City city = processCityInput(cmd.name(), cmd.ibgeCodeString(), null, currentCityProblems);

      if (!currentCityProblems.isEmpty()) {
        allCollectedProblems.addAll(currentCityProblems);
      } else {
        String ibgeCodeStr = city.getIbgeCode().toString();
        if (!ibgeCodesInPayload.add(ibgeCodeStr)) {
          allCollectedProblems.add(new AppValidationException.Problem(GeoErrorCodes.CITY_ALREADY_EXISTS, "ibgeCode"));
        }
        citiesToPersist.add(city);
      }
    }

    if (!allCollectedProblems.isEmpty()) {
      throw new AppValidationException(allCollectedProblems);
    }

    List<String> ibgeCodesToPersist = citiesToPersist.stream()
            .map(c -> c.getIbgeCode().toString())
            .toList();

    if (repo.existsAnyByIbgeCodeIn(ibgeCodesToPersist)) {
      throw new DuplicateResourceException(GeoErrorCodes.CITY_ALREADY_EXISTS);
    }

    return repo.persistAll(citiesToPersist);
  }

  @Transactional
  @Override
  public City update(UUID id, CityCreateOrUpdateCommand cmd) {
    City current = getById(id);

    List<AppValidationException.Problem> problems = new ArrayList<>();
    String nameFromCommand = cmd.name() != null ? cmd.name() : current.getName();

    City updated = processCityInput(nameFromCommand, cmd.ibgeCodeString(), current, problems);

    if (!problems.isEmpty()) {
      throw new AppValidationException(problems);
    }

    if (!updated.getIbgeCode().equals(current.getIbgeCode()) && existsByIbge(updated.getIbgeCode())) {
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
    try {
      return repo.findOptionalById(id)
              .orElseThrow(
                      () -> new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id)));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: City with ID %s in DB violates domain rules. Problems: %s",
              id, e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("id", id));
    }
  }

  @Override
  public City getByIbge(IbgeCode ibgeCode) {
    try {
      return repo.findOptionalByIbgeCode(ibgeCode.toString())
              .orElseThrow(
                      () ->
                              new ResourceNotFoundException(
                                      GeoErrorCodes.CITY_NOT_FOUND, Map.of("code", ibgeCode.toString())));
    } catch (AppValidationException e) {
      LOG.errorf(e, "Data integrity error: City with IBGE code %s in DB violates domain rules. Problems: %s",
              ibgeCode.toString(), e.getProblems().stream().map(p -> p.code().getBundleKey() + (p.fieldName() != null ? "(" + p.fieldName() + ")" : "")).collect(Collectors.joining(", ")));
      throw new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, Map.of("code", ibgeCode.toString()));
    }
  }

  @Override
  public boolean existsByIbge(IbgeCode ibgeCode) {
    if (ibgeCode == null) {
      return false;
    }
    return repo.existsByIbgeCode(ibgeCode.toString());
  }
}