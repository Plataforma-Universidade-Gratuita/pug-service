package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.ISchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.service.ISchoolReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Read-only application service for Schools.
 */
@ApplicationScoped
public class SchoolReadService implements ISchoolReadService {

  @Inject
  ISchoolQueries queries;

  @Override
  public SchoolView getViewById(UUID id) {
    return queries
            .findOptionalById(id)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("id", id)));
  }

  @Override
  public SchoolView getByName(String name) {
    if (StringUtils.isEmpty(name)) {
      throw new ResourceNotFoundException(AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", name));
    }
    return queries
            .findOptionalByName(name)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    AcademicErrorCodes.SCHOOL_NOT_FOUND, Map.of("name", name)));
  }

  @Override
  public List<SchoolView> listAll() {
    return queries.listAllSchools();
  }

  @Override
  public List<SchoolView> searchByName(String key) {
    return queries.searchByName(StringUtils.fold(key));
  }
}