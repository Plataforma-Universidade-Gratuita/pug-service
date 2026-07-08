package br.org.catolicasc.pug.academic.infra.read.dtos;

import br.org.catolicasc.pug.identity.infra.read.dtos.AccountComplexSearchView;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Read-only projection returned by former-student complex-search queries.
 *
 * @param account lightweight account projection
 * @param academicRegistration academic registration string
 * @param campus campus enumeration
 * @param requiredHours total required counterpart hours
 * @param completedHours total completed counterpart hours
 * @param concluded whether the counterpart-hours requirement has been concluded
 * @param startDate academic-period start date
 * @param dueDate academic-period due date
 * @param createdAt former-student record creation timestamp
 * @param updatedAt former-student record update timestamp
 * @param course lightweight course projection
 */
public record FormerStudentComplexSearchView(
    AccountComplexSearchView account,
    String academicRegistration,
    Campi campus,
    BigDecimal requiredHours,
    BigDecimal completedHours,
    Boolean concluded,
    LocalDate startDate,
    LocalDate dueDate,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    CourseComplexSearchView course) {}
