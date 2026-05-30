package br.org.catolicasc.pug.academic.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FormerStudentsQueriesImplTest extends BaseSearchTest {

  @Inject FormerStudentsQueriesImpl queries;
  @Inject TestDataFactory factory;

  private FormerStudent formerStudent;
  private Account account;
  private Course course;
  private User user;

  @BeforeEach
  void setup() {
    user = factory.createUser();
    account = factory.createAccount(user, AccountType.FORMER_STUDENT);
    School school = factory.createSchool();
    course = factory.createCourse(school);
    formerStudent = factory.createStudent(account, course);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve FormerStudentView by Registration")
  void shouldFindByRegistration() {
    var view =
        queries.findOptionalByAcademicRegistration(
            formerStudent.getAcademicRegistration().getValue());

    assertThat(view).isPresent();
    assertThat(view.get().academicRegistration())
        .isEqualTo(formerStudent.getAcademicRegistration().getValue());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for null registration")
  void shouldReturnEmptyForNullRegistration() {
    assertThat(queries.findOptionalByAcademicRegistration(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for empty registration")
  void shouldReturnEmptyForEmptyRegistration() {
    assertThat(queries.findOptionalByAcademicRegistration("")).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent registration")
  void shouldReturnEmptyForNonExistentRegistration() {
    assertThat(queries.findOptionalByAcademicRegistration("XXXXX")).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find by account ID")
  void shouldFindById() {
    var view = queries.findOptionalById(formerStudent.getAccountId());

    assertThat(view).isPresent();
    assertThat(view.get().accountId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for null account ID")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent account ID")
  void shouldReturnEmptyForNonExistentId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for null email")
  void shouldReturnEmptyForNullEmail() {
    assertThat(queries.findOptionalByEmail(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for empty email")
  void shouldReturnEmptyForEmptyEmail() {
    assertThat(queries.findOptionalByEmail("")).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should find by email")
  void shouldFindByEmail() {
    var view = queries.findOptionalByEmail(account.getEmail().getValue());

    assertThat(view).isPresent();
    assertThat(view.get().accountId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for null CPF")
  void shouldReturnEmptyForNullCpf() {
    assertThat(queries.findOptionalByCpf(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for empty CPF")
  void shouldReturnEmptyForEmptyCpf() {
    assertThat(queries.findOptionalByCpf("")).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all by course ID")
  void shouldListAllByCourseId() {
    var list = queries.listAllByCourseId(course.getId());
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null course ID")
  void shouldReturnEmptyListForNullCourseId() {
    assertThat(queries.listAllByCourseId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all students")
  void shouldListAllStudents() {
    var list = queries.listAllStudents();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list views by account IDs")
  void shouldListViewsByAccountIds() {
    var list = queries.listViewsByAccountIds(List.of(formerStudent.getAccountId()));
    assertThat(list).hasSize(1);
    assertThat(list.get(0).accountId()).isEqualTo(formerStudent.getAccountId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null account IDs")
  void shouldReturnEmptyListForNullAccountIds() {
    assertThat(queries.listViewsByAccountIds(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for empty account IDs")
  void shouldReturnEmptyListForEmptyAccountIds() {
    assertThat(queries.listViewsByAccountIds(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should verify all fields in formerStudent view")
  void shouldVerifyAllFieldsInView() {
    var view =
        queries.findOptionalByAcademicRegistration(
            formerStudent.getAcademicRegistration().getValue());

    assertThat(view).isPresent();
    var sv = view.get();
    assertThat(sv.accountId()).isEqualTo(formerStudent.getAccountId());
    assertThat(sv.campus()).isEqualTo(formerStudent.getCampus());
    assertThat(sv.courseId()).isEqualTo(formerStudent.getCourseId());
    assertThat(sv.requiredHours())
        .isEqualByComparingTo(formerStudent.getCounterpartHours().getRequiredHours());
    assertThat(sv.completedHours())
        .isEqualByComparingTo(formerStudent.getCounterpartHours().getCompletedHours());
    assertThat(sv.concluded()).isEqualTo(formerStudent.getCounterpartHours().getConcluded());
    assertThat(sv.startDate()).isEqualTo(formerStudent.getPeriod().getStartDate());
    assertThat(sv.dueDate()).isEqualTo(formerStudent.getPeriod().getDueDate());
    assertThat(sv.createdAt()).isNotNull();
    assertThat(sv.updatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Should search students by name successfully")
  void shouldSearchByNameSuccess() throws Exception {
    syncIndex(UserEntity.class);

    String searchKey = user.getName().split(" ")[0];
    var results = queries.searchByName(searchKey);

    assertThat(results).anyMatch(v -> v.accountId().equals(formerStudent.getAccountId()));
  }

  @Test
  @DisplayName("Should return empty list for non-existent search query")
  void shouldReturnEmptyForNoMatches() throws Exception {
    syncIndex(UserEntity.class);
    assertThat(queries.searchByName("NonExistentUser123")).isEmpty();
  }

  @Test
  @DisplayName("Should handle invalid search inputs gracefully")
  void shouldHandleInvalidSearchInputs() {
    assertSearchHandlesInvalidInput(queries::searchByName);
  }
}
