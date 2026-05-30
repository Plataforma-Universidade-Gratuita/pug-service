package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.AttendanceView;
import br.org.catolicasc.pug.project.service.dtos.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-side contract for attendance projections used by detail, listing, and complex-search
 * endpoints.
 */
public interface AttendancesQueries {

  /**
   * Retrieves a read-only view of an attendance record based on its unique identifier.
   *
   * @param id the unique identifier of the attendance record
   * @return an {@link Optional} containing the matching projection when it exists
   */
  Optional<AttendanceView> findOptionalById(UUID id);

  /**
   * Retrieves every attendance projection currently available in the system.
   *
   * @return the complete collection of attendance projections
   */
  List<AttendanceView> listAll();

  /**
   * Retrieves the attendance projections associated with the provided identifiers.
   *
   * @param ids the identifiers used to restrict the query
   * @return the subset of attendance projections matching the provided identifiers
   */
  List<AttendanceView> listAllByIds(List<UUID> ids);

  /**
   * Executes a paginated attendance complex-search query.
   *
   * @param criteria the optional filtering criteria used to constrain the query
   * @param pageQuery the normalized paging request
   * @return a paginated collection of matching attendance projections
   */
  PageResult<AttendanceView> search(AttendanceComplexSearchCriteria criteria, PageQuery pageQuery);
}
