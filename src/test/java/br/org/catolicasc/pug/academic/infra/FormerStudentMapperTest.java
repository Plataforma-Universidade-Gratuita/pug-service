package br.org.catolicasc.pug.academic.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.vos.AcademicRegistration;
import br.org.catolicasc.pug.academic.domain.vos.CounterpartHours;
import br.org.catolicasc.pug.academic.domain.vos.Period;
import br.org.catolicasc.pug.academic.infra.persistence.FormerStudentEntity;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FormerStudentMapper Tests")
class FormerStudentMapperTest extends CopyableMapperTest<FormerStudent, FormerStudentEntity> {

  @Override
  protected FormerStudent createDomain() {
    return FormerStudent.factory(
        UuidCreator.getTimeOrderedEpoch(),
        AcademicRegistration.factory("12345"),
        Campi.JARAGUA_DO_SUL,
        UuidCreator.getTimeOrderedEpoch(),
        CounterpartHours.factory(new BigDecimal("100"), BigDecimal.ZERO, false),
        Period.factory(LocalDate.now(), LocalDate.now().plusMonths(6)));
  }

  @Override
  protected FormerStudentEntity createEntity() {
    return FormerStudentEntity.builder().academicRegistration("99999").build();
  }

  @Override
  protected FormerStudent mapToDomain(FormerStudentEntity entity) {
    return FormerStudentMapper.toDomain(entity);
  }

  @Override
  protected FormerStudentEntity mapToEntity(FormerStudent domain) {
    return FormerStudentMapper.toEntity(domain);
  }

  @Override
  protected void copy(FormerStudent domain, FormerStudentEntity entity) {
    FormerStudentMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(FormerStudent original, FormerStudent mapped) {
    assertThat(mapped.getAccountId()).isEqualTo(original.getAccountId());
    assertThat(mapped.getAcademicRegistration().getValue())
        .isEqualTo(original.getAcademicRegistration().getValue());
    assertThat(mapped.getCounterpartHours().getRequiredHours())
        .isEqualTo(original.getCounterpartHours().getRequiredHours());
    assertThat(mapped.getCampus()).isEqualTo(original.getCampus());
    assertThat(mapped.getCourseId()).isEqualTo(original.getCourseId());
    assertThat(mapped.getCounterpartHours().getCompletedHours())
        .isEqualByComparingTo(original.getCounterpartHours().getCompletedHours());
    assertThat(mapped.getCounterpartHours().getConcluded())
        .isEqualTo(original.getCounterpartHours().getConcluded());
    assertThat(mapped.getPeriod().getStartDate()).isEqualTo(original.getPeriod().getStartDate());
    assertThat(mapped.getPeriod().getDueDate()).isEqualTo(original.getPeriod().getDueDate());
    assertThat(mapped.getAuditInfo().getCreatedAt())
        .isEqualTo(original.getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    FormerStudent formerStudent = createDomain();
    FormerStudentEntity entity = createEntity();

    FormerStudentMapper.copy(formerStudent, entity);

    assertThat(entity.getAcademicRegistration())
        .isEqualTo(formerStudent.getAcademicRegistration().getValue());
    assertThat(entity.getCampus()).isEqualTo(formerStudent.getCampus());
    assertThat(entity.getCourseId()).isEqualTo(formerStudent.getCourseId());
    assertThat(entity.getRequiredHours())
        .isEqualByComparingTo(formerStudent.getCounterpartHours().getRequiredHours());
    assertThat(entity.getCompletedHours())
        .isEqualByComparingTo(formerStudent.getCounterpartHours().getCompletedHours());
    assertThat(entity.getConcluded()).isEqualTo(formerStudent.getCounterpartHours().getConcluded());
    assertThat(entity.getStartDate()).isEqualTo(formerStudent.getPeriod().getStartDate());
    assertThat(entity.getDueDate()).isEqualTo(formerStudent.getPeriod().getDueDate());
    assertThat(entity.getCreatedAt()).isEqualTo(formerStudent.getAuditInfo().getCreatedAt());
    assertThat(entity.getUpdatedAt()).isEqualTo(formerStudent.getAuditInfo().getUpdatedAt());
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(FormerStudentMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("toView should map all fields correctly")
  void toViewShouldMapAllFields() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    UUID courseId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate today = LocalDate.now();

    FormerStudentEntity entity =
        FormerStudentEntity.builder()
            .accountId(accountId)
            .academicRegistration("54321")
            .campus(Campi.JARAGUA_DO_SUL)
            .courseId(courseId)
            .requiredHours(new BigDecimal("200"))
            .completedHours(new BigDecimal("50"))
            .concluded(false)
            .startDate(today)
            .dueDate(today.plusMonths(12))
            .createdAt(now)
            .updatedAt(now)
            .build();

    FormerStudentView view = FormerStudentMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.accountId()).isEqualTo(accountId);
    assertThat(view.academicRegistration()).isEqualTo("54321");
    assertThat(view.campus()).isEqualTo(Campi.JARAGUA_DO_SUL);
    assertThat(view.courseId()).isEqualTo(courseId);
    assertThat(view.requiredHours()).isEqualByComparingTo(new BigDecimal("200"));
    assertThat(view.completedHours()).isEqualByComparingTo(new BigDecimal("50"));
    assertThat(view.concluded()).isFalse();
    assertThat(view.startDate()).isEqualTo(today);
    assertThat(view.dueDate()).isEqualTo(today.plusMonths(12));
  }
}
