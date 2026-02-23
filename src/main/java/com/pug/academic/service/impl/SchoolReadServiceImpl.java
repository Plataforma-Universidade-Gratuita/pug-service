package com.pug.academic.service.impl;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.SchoolQueries;
import com.pug.academic.infra.read.dtos.SchoolView;
import com.pug.academic.service.SchoolReadService;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/** Read-only application service for Schools. */
@ApplicationScoped
public class SchoolReadServiceImpl implements SchoolReadService {

  private static final Logger LOG = Logger.getLogger(SchoolReadServiceImpl.class);

  @Inject SchoolQueries queries;

  @Override
  public SchoolView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("School lookup failed: ID %s not found", id);
              return new ResourceNotFoundException(
                  AcademicErrorCodes.SCHOOL_NOT_FOUND, "id", id.toString());
            });
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
