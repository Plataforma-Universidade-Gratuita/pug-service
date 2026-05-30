package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EnrollmentQueriesImplTest {

  @Inject EnrollmentsQueriesImpl queries;
  @Inject TestDataFactory factory;

  private FormerStudent formerStudent;
  private Project project;

  @BeforeEach
  void setup() {
    School areaOfExpertise = factory.createSchool();
    Course course = factory.createCourse(areaOfExpertise);
    Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(acc, course);

    Entity entity = factory.createEntity(factory.getAnyCity());
    project =
        factory.createProject(
            entity, factory.createAccount(factory.createUser(), AccountType.PARTNER));

    factory.createEnrollment(formerStudent, project);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve EnrollmentView by composite ID")
  void shouldFindByIds() {
    var view = queries.findOptionalByIds(project.getId(), formerStudent.getAccountId());

    assertThat(view).isPresent();
    assertThat(view.get().projectId()).isEqualTo(project.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when project ID is null")
  void shouldReturnEmptyForNullProjectId() {
    assertThat(queries.findOptionalByIds(null, formerStudent.getAccountId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when formerStudent ID is null")
  void shouldReturnEmptyForNullStudentId() {
    assertThat(queries.findOptionalByIds(project.getId(), null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent composite ID")
  void shouldReturnEmptyForNonExistentIds() {
    assertThat(
            queries.findOptionalByIds(
                UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch()))
        .isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all enrollments")
  void shouldListAllEnrollments() {
    var list = queries.listAll();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list enrollments by project ID")
  void shouldListByProjectId() {
    var list = queries.listAllByProjectId(project.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.projectId()).isEqualTo(project.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null project ID")
  void shouldReturnEmptyListForNullProjectId() {
    assertThat(queries.listAllByProjectId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list enrollments by formerStudent ID")
  void shouldListByStudentId() {
    var list = queries.listAllByStudentId(formerStudent.getAccountId());
    assertThat(list).isNotEmpty();
    assertThat(list)
        .allSatisfy(v -> assertThat(v.formerStudentId()).isEqualTo(formerStudent.getAccountId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null formerStudent ID")
  void shouldReturnEmptyListForNullStudentId() {
    assertThat(queries.listAllByStudentId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should verify all fields in enrollment view")
  void shouldVerifyAllFieldsInView() {
    var view = queries.findOptionalByIds(project.getId(), formerStudent.getAccountId());

    assertThat(view).isPresent();
    var ev = view.get();
    assertThat(ev.projectId()).isEqualTo(project.getId());
    assertThat(ev.formerStudentId()).isEqualTo(formerStudent.getAccountId());
    assertThat(ev.status()).isNotNull();
    assertThat(ev.createdAt()).isNotNull();
    assertThat(ev.updatedAt()).isNotNull();
  }

  @Test
  @Transactional
  @DisplayName("Should search enrollments by project, student, and status")
  void shouldSearchByFilters() {
    var result =
        queries.search(
            new EnrollmentComplexSearchCriteria(
                List.of(project.getId()),
                List.of(formerStudent.getAccountId()),
                List.of(EnrollmentStatus.PENDING),
                null,
                null,
                null,
                null),
            new PageQuery(0, 1));

    assertThat(result.content()).isNotEmpty();
    assertThat(result.content())
        .allSatisfy(
            view -> {
              assertThat(view.projectId()).isEqualTo(project.getId());
              assertThat(view.formerStudentId()).isEqualTo(formerStudent.getAccountId());
              assertThat(view.status()).isEqualTo(EnrollmentStatus.PENDING);
            });
  }
}
