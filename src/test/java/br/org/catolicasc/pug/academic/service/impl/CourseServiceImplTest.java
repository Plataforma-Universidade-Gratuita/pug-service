package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.CourseUpdateCommand;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CourseServiceImpl Integration Tests")
class CourseServiceImplTest {

  @Inject CourseServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;

  @Test
  @Transactional
  @DisplayName("Should save course successfully")
  void saveSuccess() {
    School school = factory.createSchool();
    em.flush();

    CourseCreateCommand cmd = new CourseCreateCommand("CS " + UUID.randomUUID(), school.getId());
    Course saved = service.save(cmd);

    assertThat(saved.getName()).startsWith("CS ");
    assertThat(saved.getSchoolId()).isEqualTo(school.getId());
    verify(audit).fireCreate(Course.class.getName(), saved.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when school not found on save")
  void saveSchoolNotFound() {
    CourseCreateCommand cmd = new CourseCreateCommand("CS", UUID.randomUUID());
    assertThrows(ResourceNotFoundException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same name")
  void saveDuplicate() {
    School school = factory.createSchool();
    Course existing = factory.createCourse(school);
    em.flush();

    CourseCreateCommand cmd = new CourseCreateCommand(existing.getName(), school.getId());
    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception for blank name")
  void saveValidationError() {
    School school = factory.createSchool();
    em.flush();

    CourseCreateCommand cmd = new CourseCreateCommand("   ", school.getId());
    assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should get course by ID")
  void getByIdSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    Course found = service.getById(course.getId());
    assertThat(found.getId()).isEqualTo(course.getId());
  }

  @Test
  @DisplayName("Should throw when course not found")
  void getByIdNotFound() {
    assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
  }

  @Test
  @Transactional
  @DisplayName("Should update course successfully")
  void updateSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    String newName = "Updated Course " + UUID.randomUUID();
    CourseUpdateCommand cmd = new CourseUpdateCommand(newName, null);
    Course updated = service.update(course.getId(), cmd);

    assertThat(updated.getName()).isEqualTo(newName);
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should update course with new school")
  void updateWithNewSchool() {
    School school1 = factory.createSchool();
    School school2 = factory.createSchool();
    Course course = factory.createCourse(school1);
    em.flush();

    CourseUpdateCommand cmd = new CourseUpdateCommand(null, school2.getId());
    Course updated = service.update(course.getId(), cmd);

    assertThat(updated.getSchoolId()).isEqualTo(school2.getId());
  }

  @Test
  @DisplayName("Should throw when updating non-existing course")
  void updateNotFound() {
    CourseUpdateCommand cmd = new CourseUpdateCommand("Name", null);
    assertThrows(ResourceNotFoundException.class, () -> service.update(UUID.randomUUID(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete course successfully")
  void deleteSuccess() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);
    em.flush();

    boolean deleted = service.delete(course.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Course.class.getName(), course.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @DisplayName("Should return false when deleting non-existing course")
  void deleteNonExisting() {
    assertThat(service.delete(UUID.randomUUID())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting course with students")
  void deleteWithStudents() {
    School school = factory.createSchool();
    Course course = factory.createCourse(school);

    var user = factory.createUser();
    var account =
        factory.createAccount(user, br.org.catolicasc.pug.shared.domain.enums.AccountType.STUDENT);
    factory.createStudent(account, course);
    em.flush();

    assertThrows(BusinessRuleException.class, () -> service.delete(course.getId()));
  }

  @Test
  @DisplayName("Should delegate existsAnyBySchoolId to repo")
  void existsAnyBySchoolId() {
    assertThat(service.existsAnyBySchoolId(UUID.randomUUID())).isFalse();
  }
}
