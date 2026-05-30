package br.org.catolicasc.pug.academic.service.dtos.courses;

import java.util.List;
import java.util.UUID;

/**
 * Service-layer criteria DTO used to execute academic-course complex-search operations.
 *
 * @param name optional course-name fragment used in a {@code like} filter
 * @param areaOfExpertiseIds optional area-of-expertise identifiers used in an {@code in} filter
 */
public record CourseComplexSearchCriteria(String name, List<UUID> areaOfExpertiseIds) {

  /**
   * Creates immutable academic-course complex-search criteria for the application service layer.
   *
   * <p>The {@code areaOfExpertiseIds} collection is defensively copied so downstream query code can
   * safely treat it as immutable.
   */
  public CourseComplexSearchCriteria {
    areaOfExpertiseIds = areaOfExpertiseIds == null ? List.of() : List.copyOf(areaOfExpertiseIds);
  }
}
