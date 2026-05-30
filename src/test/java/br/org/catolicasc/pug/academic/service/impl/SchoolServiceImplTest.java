package br.org.catolicasc.pug.academic.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.SchoolCreateCommandBuilder.aSchoolCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.SchoolUpdateCommandBuilder.aSchoolUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import br.org.catolicasc.pug.academic.domain.School;
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
@DisplayName("SchoolServiceImpl Integration Tests")
class SchoolServiceImplTest {

  @Inject SchoolServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;

  @Test
  @Transactional
  @DisplayName("Should save areaOfExpertise successfully")
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
  @DisplayName("Should get areaOfExpertise by ID")
  void getByIdSuccess() {
    School areaOfExpertise = factory.createSchool();
    em.flush();

    School found = service.getById(areaOfExpertise.getId());
    assertThat(found.getId()).isEqualTo(areaOfExpertise.getId());
  }

  @Test
  @DisplayName("Should throw when areaOfExpertise not found")
  void getByIdNotFound() {
    assertThrows(
        ResourceNotFoundException.class, () -> service.getById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @Transactional
  @DisplayName("Should update areaOfExpertise successfully")
  void updateSuccess() {
    School areaOfExpertise = factory.createSchool();
    em.flush();

    var cmd = aSchoolUpdateCommand().build();
    School updated = service.update(areaOfExpertise.getId(), cmd);

    assertThat(updated.getName()).isEqualTo(cmd.name());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException when updating to existing name")
  void updateDuplicateName() {
    School areaOfExpertise1 = factory.createSchool();
    School areaOfExpertise2 = factory.createSchool();
    em.flush();

    var cmd = aSchoolUpdateCommand().withName(areaOfExpertise2.getName()).build();
    assertThrows(
        DuplicateResourceException.class, () -> service.update(areaOfExpertise1.getId(), cmd));
  }

  @Test
  @DisplayName("Should throw when updating non-existing areaOfExpertise")
  void updateNotFound() {
    var cmd = aSchoolUpdateCommand().build();
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.update(UuidCreator.getTimeOrderedEpoch(), cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should delete areaOfExpertise successfully")
  void deleteSuccess() {
    School areaOfExpertise = factory.createSchool();
    em.flush();

    boolean deleted = service.delete(areaOfExpertise.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(School.class.getName(), areaOfExpertise.getId());
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    assertThat(service.delete(null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting non-existing areaOfExpertise")
  void deleteNonExisting() {
    assertThat(service.delete(UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting areaOfExpertise with courses")
  void deleteWithCourses() {
    School areaOfExpertise = factory.createSchool();
    factory.createCourse(areaOfExpertise);
    em.flush();

    assertThrows(BusinessRuleException.class, () -> service.delete(areaOfExpertise.getId()));
  }
}
