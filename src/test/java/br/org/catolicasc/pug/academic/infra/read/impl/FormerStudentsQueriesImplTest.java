package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentsQueriesImpl Coverage")
class FormerStudentsQueriesImplTest extends BaseSearchTest {

  @Inject FormerStudentsQueriesImpl queries;
  @Inject TestDataFactory factory;

  private AreaOfExpertise areaOfExpertise;
  private Course course;
  private FormerStudent formerStudent;
  private Account account;
  private User user;

  @BeforeEach
  void setup() {
    user = factory.createUser();
    account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    areaOfExpertise = factory.createAreaOfExpertise();
    course = factory.createCourse(areaOfExpertise);
    formerStudent = factory.createStudent(account, course);
  }

  @Test
  @Transactional
  @DisplayName("Should find former-student view by account ID")
  void findByIdSuccess() {
    var view = queries.findOptionalById(formerStudent.getAccountId());

    assertThat(view).isPresent();
    assertThat(view.get().accountId()).isEqualTo(formerStudent.getAccountId());
    assertThat(view.get().academicRegistration())
        .isEqualTo(formerStudent.getAcademicRegistration().getValue());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when account ID is null")
  void findByIdNull() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when account ID does not exist")
  void findByIdNotFound() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list former students by account IDs")
  void listAllByIdsSuccess() {
    var list = queries.listAllByIds(List.of(formerStudent.getAccountId()));

    assertThat(list).hasSize(1);
    assertThat(list.get(0).accountId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list when IDs are null or empty")
  void listAllByIdsEmptyInputs() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all former students")
  void listAllFormerStudents() {
    assertThat(queries.listAllFormerStudents()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should search former students without criteria")
  void searchWithoutCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);

    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should search former students with every supported filter")
  void searchWithAllFilters() {
    OffsetDateTime createdAt = formerStudent.getAuditInfo().getCreatedAt();

    var result =
        queries.search(
            new PageQuery(0, 10),
            new FormerStudentComplexSearchCriteria(
                user.getName().substring(0, 3),
                user.getCpf().getValue().substring(0, 3),
                account.getEmail().getValue().substring(0, 3),
                formerStudent.getAcademicRegistration().getValue().substring(0, 3),
                List.of(formerStudent.getCampus()),
                formerStudent.getPeriod().getStartDate().minusDays(1),
                formerStudent.getPeriod().getDueDate().plusDays(1),
                true,
                createdAt.minusSeconds(1),
                createdAt.plusSeconds(1),
                true,
                List.of(course.getId()),
                List.of(areaOfExpertise.getId())));

    assertThat(result.content()).anyMatch(view -> view.account().id().equals(account.getId()));
    assertThat(result.content())
        .allSatisfy(
            view -> {
              assertThat(view.course().id()).isEqualTo(course.getId());
              assertThat(view.course().areaOfExpertise().id()).isEqualTo(areaOfExpertise.getId());
            });
  }

  @Test
  @Transactional
  @DisplayName("Should search including concluded former students")
  void searchIncludingConcluded() {
    var result =
        queries.search(
            new PageQuery(0, 10),
            new FormerStudentComplexSearchCriteria(
                null,
                null,
                null,
                null,
                List.of(Campi.JARAGUA_DO_SUL, Campi.JOINVILLE),
                null,
                null,
                true,
                null,
                null,
                true,
                null,
                null));

    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should search inactive and active accounts when activeOnly is false")
  void searchActiveOnlyFalse() {
    var result =
        queries.search(
            new PageQuery(0, 10),
            new FormerStudentComplexSearchCriteria(
                null, null, null, null, null, null, null, true, null, null, false, null, null));

    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void fetchAllSuccess() {
    var result =
        queries.search(
            new PageQuery(3, 1),
            new FormerStudentComplexSearchCriteria(
                formerStudent.getAcademicRegistration().getValue().substring(0, 3),
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                null,
                true,
                null,
                null));

    assertThat(result.page()).isZero();
    assertThat(result.content().size()).isEqualTo(result.totalElements());
    assertThat(result.totalPages()).isLessThanOrEqualTo(1);
  }
}
