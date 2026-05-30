package br.org.catolicasc.pug.academic.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.CourseCreateCommandBuilder.aCourseCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.CourseUpdateCommandBuilder.aCourseUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("CoursesServiceImpl Integration Tests")
class CoursesServiceImplTest {

  @Inject CoursesServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;

  @Test
  @Transactional
  @DisplayName("Should save course successfully")
  void saveSuccess() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    em.flush();

    var cmd = aCourseCreateCommand().withAreaOfExpertiseId(areaOfExpertise.getId()).build();
    Course saved = service.save(cmd);

    assertThat(saved.getName()).isEqualTo(cmd.name());
    assertThat(saved.getAreaOfExpertiseId()).isEqualTo(areaOfExpertise.getId());
    verify(audit).fireCreate(Course.class.getName(), saved.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw when areaOfExpertise not found on save")
  void saveAreaOfExpertiseNotFound() {
    var cmd = aCourseCreateCommand().build();
    assertThrows(ResourceNotFoundException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same name")
  void saveDuplicate() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course existing = factory.createCourse(areaOfExpertise);
    em.flush();

    var cmd =
        aCourseCreateCommand()
            .withName(existing.getName())
            .withAreaOfExpertiseId(areaOfExpertise.getId())
            .build();
    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception for blank name")
  void saveValidationError() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    em.flush();

    var cmd =
        aCourseCreateCommand()
            .withName("   ")
            .withAreaOfExpertiseId(areaOfExpertise.getId())
            .build();
    assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should get course by ID")
  void getByIdSuccess() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    em.flush();

    Course found = service.getById(course.getId());
    assertThat(found.getId()).isEqualTo(course.getId());
  }

  @Test
  @DisplayName("Should throw when course not found")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should update course successfully")
  void updateSuccess() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
    em.flush();

    var cmd = aCourseUpdateCommand().withAreaOfExpertiseId(null).build();
    Course updated = service.update(course.getId(), cmd);

    assertThat(updated.getName()).isEqualTo(cmd.name());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should update course with new areaOfExpertise")
  void updateWithNewAreaOfExpertise() {
    AreaOfExpertise areaOfExpertise1 = factory.createAreaOfExpertise();
    AreaOfExpertise areaOfExpertise2 = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise1);
    em.flush();

    var cmd =
        aCourseUpdateCommand()
            .withName(null)
            .withAreaOfExpertiseId(areaOfExpertise2.getId())
            .build();
    Course updated = service.update(course.getId(), cmd);

    assertThat(updated.getAreaOfExpertiseId()).isEqualTo(areaOfExpertise2.getId());
  }

  @Test
  @DisplayName("Should throw when updating non-existing course")
  void updateNotFound() {
    var cmd = aCourseUpdateCommand().build();
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.update(UuidCreator.getTimeOrderedEpoch(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete course successfully")
  void deleteSuccess() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);
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
    assertThat(service.delete(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting course with students")
  void deleteWithStudents() {
    AreaOfExpertise areaOfExpertise = factory.createAreaOfExpertise();
    Course course = factory.createCourse(areaOfExpertise);

    var user = factory.createUser();
    var account =
        factory.createAccount(
            user, br.org.catolicasc.pug.shared.domain.enums.AccountType.FORMER_STUDENT);
    factory.createStudent(account, course);
    em.flush();

    assertThrows(BusinessRuleException.class, () -> service.delete(course.getId()));
  }

  @Test
  @DisplayName("Should delegate existsAnyByAreaOfExpertiseId to repo")
  void existsAnyByAreaOfExpertiseId() {
    assertThat(service.existsAnyByAreaOfExpertiseId(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }
}
