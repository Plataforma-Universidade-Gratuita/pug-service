package br.org.catolicasc.pug.academic.presenter.dtos.formerstudents;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Request DTO consumed by the former-student complex-search endpoint.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param academicRegistration optional academic-registration fragment used in a {@code like} filter
 * @param campi optional campus collection used in an {@code in} filter
 * @param periodFrom optional lower bound applied to start and due dates
 * @param periodTo optional upper bound applied to start and due dates
 * @param includeConcluded optional flag indicating whether concluded obligations should also be
 *     returned
 * @param dateFrom optional lower bound applied to timestamp fields
 * @param dateTo optional upper bound applied to timestamp fields
 * @param activeOnly optional flag indicating whether only active accounts should be returned;
 *     defaults to {@code true}
 * @param courseIds optional course identifiers used in an {@code in} filter
 * @param schoolIds optional school identifiers used in an {@code in} filter
 */
public record FormerStudentComplexSearchRequest(
    @Pattern(regexp = ".*\\S.*") String name,
    @Pattern(regexp = ".*\\S.*") String cpf,
    @Pattern(regexp = ".*\\S.*") String email,
    @Pattern(regexp = ".*\\S.*") String academicRegistration,
    List<Campi> campi,
    LocalDate periodFrom,
    LocalDate periodTo,
    Boolean includeConcluded,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    Boolean activeOnly,
    List<UUID> courseIds,
    List<UUID> schoolIds) {

  public FormerStudentComplexSearchRequest {
    campi = campi == null ? List.of() : List.copyOf(campi);
    courseIds = courseIds == null ? List.of() : List.copyOf(courseIds);
    schoolIds = schoolIds == null ? List.of() : List.copyOf(schoolIds);
  }
}
