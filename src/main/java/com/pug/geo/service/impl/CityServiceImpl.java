package com.pug.geo.service.impl;

import com.pug.geo.domain.City;
import com.pug.geo.domain.CityRepository;
import com.pug.geo.domain.vos.IbgeCode;
import com.pug.geo.service.CityService;
import com.pug.geo.service.dtos.CityCreateCommand;
import com.pug.geo.service.dtos.CityUpdateCommand;
import com.pug.geo.service.utils.CityProcessor;
import com.pug.geo.service.utils.ExceptionHelper;
import com.pug.partner.service.EntityService;
import com.pug.shared.domain.enums.Campi;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.BusinessRuleException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Implementation of the {@link CityService} command interface.
 * <p>
 * This application-scoped service acts as the orchestrator for geographic state mutations.
 * It manages transaction boundaries, invokes pure domain logic via {@link CityProcessor},
 * enforces cross-cutting business rules (e.g., uniqueness, immutability of default cities),
 * and coordinates with the underlying {@link CityRepository}.
 */
@ApplicationScoped
public class CityServiceImpl implements CityService {

  private static final Logger LOG = Logger.getLogger(CityServiceImpl.class);

  @Inject
  CityRepository repo;
  @Inject
  EntityService entityService;

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public City save(CityCreateCommand cmd) {
    LOG.debugf("Attempting to create City: %s (IBGE: %s)", cmd.name(), cmd.ibgeCodeString());
    City cityToPersist = CityProcessor.processCreateInput(cmd.name(), cmd.ibgeCodeString());

    if (cityToPersist.hasFieldErrors()) {
      throw new AppValidationException(cityToPersist.getFieldErrors());
    }

    if (existsByIbge(cityToPersist.getIbgeCode())) {
      LOG.warnf("Creation failed: City with IBGE Code %s already exists", cityToPersist.getIbgeCode().getCode());
      throw ExceptionHelper.cityAlreadyExists();
    }

    City savedCity = repo.persist(cityToPersist);
    LOG.infof("City created successfully. ID: %s", savedCity.getId());
    return savedCity;
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public City update(UUID id, CityUpdateCommand cmd) {
    LOG.debugf("Attempting to update City ID: %s", id);

    City current = getById(id);
    ensureCityIsMutable(current);

    City updated = CityProcessor.processUpdateInput(current, cmd.name(), cmd.ibgeCodeString());

    if (updated.hasFieldErrors()) {
      throw new AppValidationException(updated.getFieldErrors());
    }

    if (!updated.getIbgeCode().equals(current.getIbgeCode())
            && existsByIbge(updated.getIbgeCode())) {
      LOG.warnf("Update failed: City ID %s tried to use existing IBGE %s", id, updated.getIbgeCode().getCode());
      throw ExceptionHelper.cityAlreadyExists();
    }

    repo.update(updated);
    LOG.infof("City updated successfully. ID: %s", id);
    return getById(id);
  }

  /**
   * {@inheritDoc}
   */
  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete City ID: %s", id);
    if (id == null) {
      return false;
    }

    var city = getById(id);
    ensureCityIsMutable(city);
    if (entityService.existsAnyByCityId(city.getId())) {
      LOG.warnf("Delete failed: City ID %s is still referenced by other entities", id);
      throw ExceptionHelper.cityStillReferencedByEntity();
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("City deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: City ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public City getById(UUID id) {
    City city = repo.findOptionalById(id)
            .orElseThrow(() -> {
              LOG.debugf("City lookup failed: ID %s not found", id);
              return ExceptionHelper.cityNotFound();
            });

    if (city.hasFieldErrors()) {
      LOG.errorf(
              "DATA CORRUPTION DETECTED: City %s violates domain rules: %s",
              id, city.getProblemsSummary());
      throw ExceptionHelper.cityNotFound();
    }

    return city;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Verifies whether a city already exists in the repository with the specified IBGE code.
   *
   * @param ibgeCode the validated {@link IbgeCode} Value Object to check
   * @return {@code true} if a matching city exists, {@code false} otherwise
   */
  private boolean existsByIbge(IbgeCode ibgeCode) {
    if (ibgeCode == null) {
      return false;
    }
    return repo.existsByIbgeCode(ibgeCode.getCode());
  }

  /**
   * Enforces the immutability rule for default system cities.
   * <p>
   * Checks if the provided {@link City} corresponds to one of the protected records defined in
   * {@link Campi} (e.g., Jaraguá do Sul, Joinville). These specific records are fundamental to
   * system integrity and must not be modified or deleted.
   *
   * @param city the {@link City} entity to validate
   * @throws BusinessRuleException if the city matches a protected default IBGE code
   * @see Campi
   */
  private void ensureCityIsMutable(City city) {
    if (Campi.getImmutableIbgeCodes().contains(city.getIbgeCode().getCode())) {
      LOG.warnf(
              "Modification blocked: City ID %s (IBGE %s) is a default system record.",
              city.getId(), city.getIbgeCode().getCode());
      throw ExceptionHelper.cityIsDefault();
    }
  }
}