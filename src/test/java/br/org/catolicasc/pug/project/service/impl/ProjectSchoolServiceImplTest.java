package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectSchool;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectSchoolServiceImpl Integration Tests")
class ProjectSchoolServiceImplTest {

  @Inject ProjectSchoolServiceImpl service;
  @Inject TestDataFactory factory;

  @InjectMock AuditPublisher audit;

  private Project project;
  private School areaOfExpertise;

  @BeforeEach
  void setup() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creator);
    areaOfExpertise = factory.createSchool();
  }

  @Test
  @Transactional
  @DisplayName("Should save project-areaOfExpertise association")
  void saveSuccess() {
    List<ProjectSchool> created = service.save(project.getId(), List.of(areaOfExpertise.getId()));

    assertThat(created).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list for null project ID")
  void saveNullProjectId() {
    assertThat(service.save(null, List.of(UuidCreator.getTimeOrderedEpoch()))).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for null areaOfExpertise IDs")
  void saveNullSchoolIds() {
    assertThat(service.save(project.getId(), null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty list for empty areaOfExpertise IDs")
  void saveEmptySchoolIds() {
    assertThat(service.save(project.getId(), List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should skip already existing associations")
  void saveSkipExisting() {
    service.save(project.getId(), List.of(areaOfExpertise.getId()));

    List<ProjectSchool> second = service.save(project.getId(), List.of(areaOfExpertise.getId()));

    assertThat(second).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should delete project-areaOfExpertise association")
  void deleteSuccess() {
    service.save(project.getId(), List.of(areaOfExpertise.getId()));

    boolean deleted = service.delete(project.getId(), areaOfExpertise.getId());

    assertThat(deleted).isTrue();
  }

  @Test
  @DisplayName("Should return false for null project ID in delete")
  void deleteNullProjectId() {
    assertThat(service.delete(null, UuidCreator.getTimeOrderedEpoch())).isFalse();
  }

  @Test
  @DisplayName("Should return false for null areaOfExpertise ID in delete")
  void deleteNullSchoolId() {
    assertThat(service.delete(UuidCreator.getTimeOrderedEpoch(), null)).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should delete all by project ID")
  void deleteAllByProjectId() {
    service.save(project.getId(), List.of(areaOfExpertise.getId()));

    long deleted = service.deleteAllByProjectId(project.getId());

    assertThat(deleted).isGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should return 0 for null project ID in deleteAllByProjectId")
  void deleteAllByProjectIdNull() {
    assertThat(service.deleteAllByProjectId(null)).isZero();
  }

  @Test
  @Transactional
  @DisplayName("Should delete all by areaOfExpertise ID")
  void deleteAllBySchoolId() {
    service.save(project.getId(), List.of(areaOfExpertise.getId()));

    long deleted = service.deleteAllBySchoolId(areaOfExpertise.getId());

    assertThat(deleted).isGreaterThanOrEqualTo(1);
  }

  @Test
  @DisplayName("Should return 0 for null areaOfExpertise ID in deleteAllBySchoolId")
  void deleteAllBySchoolIdNull() {
    assertThat(service.deleteAllBySchoolId(null)).isZero();
  }
}
