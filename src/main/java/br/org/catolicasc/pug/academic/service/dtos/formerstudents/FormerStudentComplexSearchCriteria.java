package br.org.catolicasc.pug.academic.service.dtos.formerstudents;

import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service-layer criteria DTO used to execute former-student complex-search operations.
 *
 * @param name optional user-name fragment used in a {@code like} filter
 * @param cpf optional user-CPF fragment used in a {@code like} filter
 * @param email optional account-email fragment used in a {@code like} filter
 * @param academicRegistration optional academic-registration fragment used in a {@code like} filter
 * @param campi optional campus collection used in an {@code in} filter
 * @param periodFrom optional lower bound applied to start and due dates
 * @param periodTo optional upper bound applied to start and due dates
 * @param includeConcluded whether concluded former students should also be returned
 * @param dateFrom optional lower bound applied to timestamp fields
 * @param dateTo optional upper bound applied to timestamp fields
 * @param activeOnly whether only active accounts should be returned
 * @param courseIds optional course identifiers used in an {@code in} filter
 * @param areaOfExpertiseIds optional area-of-expertise identifiers used in an {@code in} filter
 */
public record FormerStudentComplexSearchCriteria(
    String name,
    String cpf,
    String email,
    String academicRegistration,
    List<Campi> campi,
    LocalDate periodFrom,
    LocalDate periodTo,
    boolean includeConcluded,
    OffsetDateTime dateFrom,
    OffsetDateTime dateTo,
    boolean activeOnly,
    List<UUID> courseIds,
    List<UUID> areaOfExpertiseIds) {

  public FormerStudentComplexSearchCriteria {
    campi = campi == null ? List.of() : List.copyOf(campi);
    courseIds = courseIds == null ? List.of() : List.copyOf(courseIds);
    areaOfExpertiseIds = areaOfExpertiseIds == null ? List.of() : List.copyOf(areaOfExpertiseIds);
  }
}
