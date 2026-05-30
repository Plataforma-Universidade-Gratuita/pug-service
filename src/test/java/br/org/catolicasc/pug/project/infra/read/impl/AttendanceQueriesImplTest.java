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
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AttendanceQueriesImplTest {

  @Inject AttendanceQueriesImpl queries;
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
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty for non-existent ID")
  void shouldReturnEmptyForNonExistentId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list by enrollment ID")
  void shouldListByEnrollmentId() {
    var list = queries.listByEnrollmentId(project.getId(), formerStudent.getAccountId());
    assertThat(list).isNotEmpty();
    assertThat(list)
        .allSatisfy(
            v -> {
              assertThat(v.projectId()).isEqualTo(project.getId());
              assertThat(v.studentId()).isEqualTo(formerStudent.getAccountId());
            });
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null enrollment project ID")
  void shouldReturnEmptyForNullEnrollmentProjectId() {
    assertThat(queries.listByEnrollmentId(null, formerStudent.getAccountId())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null enrollment formerStudent ID")
  void shouldReturnEmptyForNullEnrollmentStudentId() {
    assertThat(queries.listByEnrollmentId(project.getId(), null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list by project ID")
  void shouldListByProjectId() {
    var list = queries.listByProjectId(project.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.projectId()).isEqualTo(project.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null project ID")
  void shouldReturnEmptyForNullProjectId() {
    assertThat(queries.listByProjectId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list by formerStudent ID")
  void shouldListByStudentId() {
    var list = queries.listByStudentId(formerStudent.getAccountId());
    assertThat(list).isNotEmpty();
    assertThat(list)
        .allSatisfy(v -> assertThat(v.studentId()).isEqualTo(formerStudent.getAccountId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null formerStudent ID")
  void shouldReturnEmptyForNullStudentId() {
    assertThat(queries.listByStudentId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all views")
  void shouldListViews() {
    var list = queries.listViews();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should verify all fields in attendance view")
  void shouldVerifyAllFieldsInView() {
    var view = queries.findOptionalById(attendance.getId());

    assertThat(view).isPresent();
    var av = view.get();
    assertThat(av.id()).isEqualTo(attendance.getId());
    assertThat(av.projectId()).isEqualTo(attendance.getEnrollmentIdentifier().getProjectId());
    assertThat(av.studentId()).isEqualTo(attendance.getEnrollmentIdentifier().getStudentId());
    assertThat(av.duration()).isEqualByComparingTo(attendance.getQrValidationInfo().getDuration());
    assertThat(av.qrValidationHash())
        .isEqualTo(attendance.getQrValidationInfo().getQrValidationHash());
    assertThat(av.status()).isNotNull();
    assertThat(av.createdAt()).isNotNull();
    assertThat(av.updatedAt()).isNotNull();
  }
}
