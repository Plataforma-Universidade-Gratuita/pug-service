package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Attendance;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.service.dtos.AttendanceComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AttendancesQueriesImpl Tests")
class AttendanceQueriesImplTest {

  @Inject AttendancesQueriesImpl queries;
  @Inject TestDataFactory factory;

  private FormerStudent formerStudent;
  private Project project;
  private Attendance attendance;

  @BeforeEach
  void setup() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    Account acc = factory.createAccount(factory.createUser(), AccountType.FORMER_STUDENT);
    formerStudent = factory.createStudent(acc, course);

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
              assertThat(view.studentId()).isEqualTo(formerStudent.getAccountId());
              assertThat(view.status()).isEqualTo(AttendanceStatus.WAITING);
            });
  }
}
