package br.org.catolicasc.pug.academic.service.impl;

import br.org.catolicasc.pug.academic.infra.read.CourseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.CourseView;
import br.org.catolicasc.pug.academic.service.CourseReadService;
import br.org.catolicasc.pug.academic.service.utils.ExceptionHelper;
import br.org.catolicasc.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;

/**
 * Implementation of the {@link CourseReadService}.
 *
 * <p>This application-scoped bean delegates read-only operations to the underlying {@link
 * CourseQueries} infrastructure component. It handles basic input validation and translates "not
 * found" states into standardized domain exceptions.
 */
@ApplicationScoped
public class CourseReadServiceImpl implements CourseReadService {

  private static final Logger LOG = Logger.getLogger(CourseReadServiceImpl.class);

  @Inject CourseQueries queries;

  /** {@inheritDoc} */
  @Override
  public CourseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () -> {
              LOG.debugf("Course lookup failed: ID %s not found", id);
              return ExceptionHelper.courseNotFound();
            });
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listViews() {
    return queries.listAllCourses();
  }

  /** {@inheritDoc} */
  @Override
  public List<CourseView> listViewsBySchoolId(UUID schoolId) {
    if (schoolId == null) {
      return List.of();
    }
    return queries.listAllBySchoolId(schoolId);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Prior to execution, the input query is "folded" (lowercased and accents removed via {@link
   * StringUtils#fold(String)}) to ensure maximum compatibility with the underlying search indexing
   * rules.
   */
  @Override
  public List<CourseView> searchByName(String query) {
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}
