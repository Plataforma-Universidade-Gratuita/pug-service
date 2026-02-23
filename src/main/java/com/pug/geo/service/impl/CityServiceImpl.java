package com.pug.geo.service.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.domain.enums.GeoErrorCodes;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.CityService;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
import com.pug.geo.service.utils.CityProcessor;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.BusinessRuleException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service for managing cities. */
@ApplicationScoped
public class CityServiceImpl implements CityService {

  private static final Logger LOG = Logger.getLogger(CityServiceImpl.class);

  @Inject CityRepository repo;

  @Transactional
  @Override
  public City save(CityCreateCommand cmd) {
    LOG.debugf("Attempting to create City: %s (IBGE: %s)", cmd.name(), cmd.ibgeCodeString());
    City cityToPersist = CityProcessor.processCreateInput(cmd.name(), cmd.ibgeCodeString());

    if (cityToPersist.hasErrors()) {
      throw new AppValidationException(cityToPersist.getProblems());
    }

    if (existsByIbge(cityToPersist.getIbgeCode())) {
      LOG.warnf(
          "Creation failed: City with IBGE Code %s already exists", cityToPersist.getIbgeCode());
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, "ibgeCode", cityToPersist.getIbgeCode().toString());
    }

    City savedCity = repo.persist(cityToPersist);
    LOG.infof("City created successfully. ID: %s", savedCity.getId());
    return savedCity;
  }

  @Transactional
  @Override
  public City update(UUID id, CityUpdateCommand cmd) {
    LOG.debugf("Attempting to update City ID: %s", id);
    City current = getById(id);
    ensureCityIsMutable(current);

    City updated = CityProcessor.processUpdateInput(current, cmd.name(), cmd.ibgeCodeString());

    if (updated.hasErrors()) {
      throw new AppValidationException(updated.getProblems());
    }

    if (!updated.getIbgeCode().equals(current.getIbgeCode())
        && existsByIbge(updated.getIbgeCode())) {
      LOG.warnf(
          "Update failed: City ID %s tried to use existing IBGE %s", id, updated.getIbgeCode());
      throw new DuplicateResourceException(
          GeoErrorCodes.CITY_ALREADY_EXISTS, "ibgeCode", updated.getIbgeCode().toString());
    }

    repo.update(updated);
    LOG.infof("City updated successfully. ID: %s", id);
    return getById(id);
  }

  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete City ID: %s", id);
    if (id == null) {
      return false;
    }
    var city = getById(id);
    ensureCityIsMutable(city);

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("City deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: City ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  @Override
  public City getById(UUID id) {
    City city =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("City lookup failed: ID %s not found", id);
                  return new ResourceNotFoundException(
                      GeoErrorCodes.CITY_NOT_FOUND, "id", id.toString());
                });

    if (city.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: City %s violates domain rules: %s",
          id, city.getProblemsSummary());
      throw new ResourceNotFoundException(GeoErrorCodes.CITY_NOT_FOUND, "id", id.toString());
    }

    return city;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Check if a city exists by its IBGE code.
   *
   * @param ibgeCode the IBGE code (already a validated Value Object).
   * @return true if the city exists, false otherwise.
   */
  private boolean existsByIbge(IbgeCode ibgeCode) {
    if (ibgeCode == null) {
      return false;
    }
    return repo.existsByIbgeCode(ibgeCode.toString());
  }

  /**
   * Enforces the immutability rule for default system cities.
   *
   * <p>Checks if the provided {@link City} corresponds to one of the protected records defined in
   * {@link Campi} (e.g., Jaraguá do Sul, Joinville). These specific records are fundamental to
   * system integrity and must not be modified or deleted.
   *
   * @param city the {@link City} entityId to validate.
   * @throws BusinessRuleException if the city matches a protected default IBGE code.
   * @see Campi
   */
  private void ensureCityIsMutable(City city) {
    if (Campi.getImmutableIbgeCodes().contains(city.getIbgeCode().toString())) {
      LOG.warnf(
          "Modification blocked: City ID %s (IBGE %s) is a default system record.",
          city.getId(), city.getIbgeCode());
      throw new BusinessRuleException(GeoErrorCodes.CITY_IS_DEFAULT, "id", city.getId().toString());
    }
  }
}
