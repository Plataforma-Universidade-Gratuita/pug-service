package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.service.dtos.attendance.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancesQueriesImpl Coverage")
class AttendanceQueriesImplTest {

  @Inject AttendancesQueriesImpl queries;
  @Inject TestDataFactory factory;

  private FormerStudent formerStudent;
  private Project project;
  private Attendance attendance;

  @BeforeEach
  void setup() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    Account sAcc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(sAcc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    project =
        factory.createProject(
            entity, factory.createAccount(factory.createUser(), AccountType.PARTNER));

    factory.createEnrollment(formerStudent, project);
    attendance = factory.createAttendance(project, formerStudent);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve AttendanceView by ID")
  void shouldFindById() {
    var view = queries.findOptionalById(attendance.getId());

    assertThat(view).isPresent();
    assertThat(view.get().id()).isEqualTo(attendance.getId());
    assertThat(view.get().projectName()).isEqualTo(project.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID does not exist")
  void shouldReturnEmptyForNonExistingId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all attendances")
  void shouldListAll() {
    assertThat(queries.listAll()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list attendances by IDs")
  void shouldListAllByIds() {
    var list = queries.listAllByIds(List.of(attendance.getId()));

    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(view -> assertThat(view.id()).isEqualTo(attendance.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when listing by null or empty IDs")
  void shouldReturnEmptyForNullOrEmptyIds() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should search attendances by project, student, and status")
  void shouldSearchByFilters() {
    var result =
        queries.search(
            new AttendanceComplexSearchCriteria(
                List.of(project.getId()),
                List.of(formerStudent.getAccountId()),
                List.of(AttendanceStatus.WAITING),
                List.of(),
                null,
                null,
                null,
                null),
            new PageQuery(0, 25));

    assertThat(result.content()).isNotEmpty();
    assertThat(result.content())
        .allSatisfy(
            view -> {
              assertThat(view.projectId()).isEqualTo(project.getId());
              assertThat(view.formerStudentId()).isEqualTo(formerStudent.getAccountId());
              assertThat(view.status()).isEqualTo(AttendanceStatus.WAITING);
            });
  }

  @Test
  @Transactional
  @DisplayName("Should search attendances with duration and date filters")
  void shouldSearchByDurationAndDateFilters() {
    OffsetDateTime createdAt = attendance.getAttendanceInfo().getAuditInfo().getCreatedAt();

    var result =
        queries.search(
            new AttendanceComplexSearchCriteria(
                List.of(project.getId()),
                List.of(formerStudent.getAccountId()),
                List.of(AttendanceStatus.WAITING),
                List.of(),
                BigDecimal.ZERO,
                attendance.getQrValidationInfo().getDuration().add(BigDecimal.ONE),
                createdAt.minusSeconds(1),
                createdAt.plusSeconds(1)),
            new PageQuery(0, 10));

    assertThat(result.content()).anyMatch(view -> view.id().equals(attendance.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should search attendances without criteria")
  void shouldSearchWithoutCriteria() {
    var result = queries.search(null, new PageQuery(0, 10));

    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should use default page query when page query is null")
  void shouldSearchWithNullPageQuery() {
    var result =
        queries.search(
            new AttendanceComplexSearchCriteria(null, null, null, null, null, null, null, null),
            null);

    assertThat(result.content()).hasSizeLessThanOrEqualTo(25);
  }

  @Test
  @Transactional
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void shouldFetchAllWhenPageSizeIsOne() {
    var result =
        queries.search(
            new AttendanceComplexSearchCriteria(
                List.of(project.getId()), null, null, null, null, null, null, null),
            new PageQuery(7, 1));

    assertThat(result.page()).isZero();
    assertThat(result.content().size()).isEqualTo(result.totalElements());
    assertThat(result.totalPages()).isLessThanOrEqualTo(1);
  }
}
