package com.pug.academic.service.impl;

import com.pug.academic.domain.School;
import com.pug.academic.domain.SchoolRepository;
import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.service.SchoolService;
import com.pug.academic.service.dtos.SchoolCreateCommand;
import com.pug.academic.service.dtos.SchoolUpdateCommand;
import com.pug.academic.service.utils.SchoolProcessor;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.exceptions.DuplicateResourceException;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Service class for managing School entities. */
@ApplicationScoped
public class SchoolServiceImpl implements SchoolService {

  private static final Logger LOG = Logger.getLogger(SchoolServiceImpl.class);

  @Inject SchoolRepository repo;

  @Transactional
  @Override
  public School save(SchoolCreateCommand cmd) {
    LOG.debugf("Attempting to create School: %s", cmd.name());
    School schoolToPersist = SchoolProcessor.processCreateInput(cmd.name());

    if (schoolToPersist.hasErrors()) {
      throw new AppValidationException(schoolToPersist.getProblems());
    }

    if (existsByName(schoolToPersist.getName())) {
      LOG.warnf("Creation failed: School with name %s already exists", schoolToPersist.getName());
      throw new DuplicateResourceException(
          AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, "name", schoolToPersist.getName());
    }

    School savedSchool = repo.persist(schoolToPersist);
    LOG.infof("School created successfully. ID: %s", savedSchool.getId());
    return savedSchool;
  }

  @Transactional
  @Override
  public School update(UUID id, SchoolUpdateCommand cmd) {
    LOG.debugf("Attempting to update School ID: %s", id);
    School current = getById(id);
    School updatedSchool = SchoolProcessor.processUpdateInput(current, cmd.name());

    if (updatedSchool.hasErrors()) {
      throw new AppValidationException(updatedSchool.getProblems());
    }

    if (!updatedSchool.getName().equals(current.getName())
        && existsByName(updatedSchool.getName())) {
      LOG.warnf(
          "Update failed: School ID %s tried to use existing name %s", id, updatedSchool.getName());
      throw new DuplicateResourceException(
          AcademicErrorCodes.SCHOOL_ALREADY_EXISTS, "name", updatedSchool.getName());
    }

    repo.update(updatedSchool);
    LOG.infof("School updated successfully. ID: %s", id);
    return getById(id);
  }

  @Transactional
  @Override
  public boolean delete(UUID id) {
    LOG.debugf("Attempting to delete School ID: %s", id);
    if (id == null) {
      return false;
    }

    boolean deleted = repo.deleteById(id);
    if (deleted) {
      LOG.infof("School deleted successfully. ID: %s", id);
    } else {
      LOG.debugf("Delete failed: School ID %s not found (idempotent)", id);
    }

    return deleted;
  }

  @Override
  public School getById(UUID id) {
    School school =
        repo.findOptionalById(id)
            .orElseThrow(
                () -> {
                  LOG.debugf("School lookup failed: ID %s not found", id);
                  return new ResourceNotFoundException(
                      AcademicErrorCodes.SCHOOL_NOT_FOUND, "id", id.toString());
                });

    if (school.hasErrors()) {
      LOG.errorf(
          "DATA CORRUPTION DETECTED: School %s violates domain rules: %s",
          id, school.getProblemsSummary());
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, "id", id.toString());
    }
    return school;
  }

  /* --------------- INTERNAL HELPER METHODS --------------- */

  /**
   * Checks if a School entityId exists by its name.
   *
   * @param name the name of the school.
   * @return true if a school with the given name exists, false otherwise.
   */
  private boolean existsByName(String name) {
    if (StringUtils.isEmpty(name)) {
      return false;
    }
    return repo.existsByName(name);
  }
}
