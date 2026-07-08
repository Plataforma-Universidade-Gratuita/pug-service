package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.EnrollmentView;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-side contract for retrieving enrollment projections from the persistence layer.
 *
 * <p>This boundary centralizes direct enrollment lookups, collection reads, and paginated
 * complex-search queries used by the project presenter and application read services.
 */
public interface EnrollmentsQueries {

  /**
   * Retrieves a read-only enrollment projection by its composite identifiers.
   *
   * @param projectId the unique identifier of the project linked to the enrollment
   * @param formerStudentId the unique identifier of the former student linked to the enrollment
   * @return an {@link Optional} containing the matching projection when it exists
   */
  Optional<EnrollmentView> findOptionalByIds(UUID projectId, UUID formerStudentId);

  /**
   * Retrieves every enrollment projection currently available in the persistence-backed read model.
   *
   * @return the complete collection of enrollment projections
   */
  List<EnrollmentView> listAll();

  /**
   * Retrieves every enrollment projection associated with the provided project.
   *
   * @param projectId the unique identifier of the project
   * @return the matching enrollment projections, or an empty list when none exist
   */
  List<EnrollmentView> listAllByProjectId(UUID projectId);

  /**
   * Retrieves every enrollment projection associated with the provided former student.
   *
   * @param formerStudentId the unique identifier of the former student account
   * @return the matching enrollment projections, or an empty list when none exist
   */
  List<EnrollmentView> listAllByFormerStudentId(UUID formerStudentId);

  /**
   * Executes a paginated enrollment complex-search query.
   *
   * @param criteria the optional filtering criteria used to constrain the query
   * @param pageQuery the normalized paging request
   * @return a paginated collection of matching enrollment projections
   */
  PageResult<EnrollmentView> search(EnrollmentComplexSearchCriteria criteria, PageQuery pageQuery);
}
