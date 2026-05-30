package br.org.catolicasc.pug.project.service.impl;

import br.org.catolicasc.pug.project.infra.read.AttendancesQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.service.AttendancesReadService;
import br.org.catolicasc.pug.project.service.dtos.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.project.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import br.org.catolicasc.pug.shared.utils.CollectionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the attendance read-side application service.
 */
@ApplicationScoped
public class AttendancesReadServiceImpl implements AttendancesReadService {

  private static final Logger LOG = Logger.getLogger(AttendancesReadServiceImpl.class);

  @Inject AttendancesQueries queries;

  @Override
  public AttendanceView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Attendance lookup failed: ID %s not found", id);
              return ExceptionHelper.attendanceNotFound();
            });
  }

  @Override
  public List<AttendanceView> listViews() {
    return queries.listAll();
  }

  @Override
  public List<AttendanceView> listViewsByIds(List<UUID> ids) {
    return CollectionUtils.isEmpty(ids) ? List.of() : queries.listAllByIds(ids);
  }

  @Override
  public PageResult<AttendanceView> search(
      AttendanceComplexSearchCriteria criteria, PageQuery pageQuery) {
    return queries.search(criteria, pageQuery);
  }
}
