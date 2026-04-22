package br.org.catolicasc.pug.academic.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.SchoolCreateCommandBuilder.aSchoolCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.SchoolUpdateCommandBuilder.aSchoolUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.project.service.ProjectSchoolService;
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
@DisplayName("SchoolServiceImpl Integration Tests")
class SchoolServiceImplTest {

  @Inject SchoolServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;
  @InjectMock ProjectSchoolService projectSchoolService;

  @Test
  @Transactional
  @DisplayName("Should save school successfully")
  void saveSuccess() {
    var cmd = aSchoolCreateCommand().build();
    School saved = service.save(cmd);

    assertThat(saved.getName()).isEqualTo(cmd.name());
    verify(audit).fireCreate(School.class.getName(), saved.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same name")
  void saveDuplicate() {
    School existing = factory.createSchool();
    em.flush();

    var cmd = aSchoolCreateCommand().withName(existing.getName()).build();
    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception for blank name")
  void saveValidationError() {
    var cmd = aSchoolCreateCommand().withName("   ").build();
    assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should get school by ID")
  void getByIdSuccess() {
    School school = factory.createSchool();
    em.flush();

    School found = service.getById(school.getId());
    assertThat(found.getId()).isEqualTo(school.getId());
  }

  @Test
  @DisplayName("Should throw when school not found")
  void getByIdNotFound() {
    assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
  }

  @Test
  @Transactional
  @DisplayName("Should update school successfully")
  void updateSuccess() {
    School school = factory.createSchool();
    em.flush();

    var cmd = aSchoolUpdateCommand().build();
    School updated = service.update(school.getId(), cmd);

    assertThat(updated.getName()).isEqualTo(cmd.name());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException when updating to existing name")
  void updateDuplicateName() {
    School school1 = factory.createSchool();
    School school2 = factory.createSchool();
    em.flush();

    var cmd = aSchoolUpdateCommand().withName(school2.getName()).build();
    assertThrows(DuplicateResourceException.class, () -> service.update(school1.getId(), cmd));
  }

  @Test
  @DisplayName("Should throw when updating non-existing school")
  void updateNotFound() {
    var cmd = aSchoolUpdateCommand().build();
    assertThrows(ResourceNotFoundException.class, () -> service.update(UUID.randomUUID(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete school successfully")
  void deleteSuccess() {
    School school = factory.createSchool();
    em.flush();

    boolean deleted = service.delete(school.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(School.class.getName(), school.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting non-existing school")
  void deleteNonExisting() {
    assertThat(service.delete(UUID.randomUUID())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting school with courses")
  void deleteWithCourses() {
    School school = factory.createSchool();
    factory.createCourse(school);
    em.flush();

    assertThrows(BusinessRuleException.class, () -> service.delete(school.getId()));
  }
}
