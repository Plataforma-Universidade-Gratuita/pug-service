package br.org.catolicasc.pug.academic.presenter.dtos;

import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO used by the academic-course complex-search endpoint.
 *
 * <p>Each field is optional. When more than one filter is provided, the search applies all of
 * them using logical {@code AND}.
 *
 * @param name optional course-name fragment used in a {@code like} filter
 * @param schoolIds optional school identifiers used in an {@code in} filter
 */
public record CourseComplexSearchRequest(@Pattern(regexp = ".*\\S.*") String name, List<UUID> schoolIds) {

  /**
   * Creates an immutable complex-search request payload for academic-course queries.
   *
   * <p>The {@code schoolIds} collection is defensively copied to prevent accidental mutation after
   * request instantiation.
   */
  public CourseComplexSearchRequest {
    schoolIds = schoolIds == null ? List.of() : List.copyOf(schoolIds);
  }
}
