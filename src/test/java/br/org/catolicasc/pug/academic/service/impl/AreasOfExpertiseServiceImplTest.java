package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.AreaOfExpertiseRepository;
import br.org.catolicasc.pug.academic.service.CoursesService;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.areasofexpertise.AreaOfExpertiseUpdateCommand;
import br.org.catolicasc.pug.project.service.ProjectAreaOfExpertiseService;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.infra.audit.AuditPublisher;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("AreasOfExpertiseServiceImpl Coverage")
class AreasOfExpertiseServiceImplTest {

  @Inject AreasOfExpertiseServiceImpl service;

  @InjectMock AreaOfExpertiseRepository repo;
  @InjectMock CoursesService coursesService;
  @InjectMock ProjectAreaOfExpertiseService projectAreaOfExpertiseService;
  @InjectMock AuditPublisher auditPublisher;

  @Test
  @DisplayName("Should get AreaOfExpertise by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AreaOfExpertise areaOfExpertise = AreaOfExpertise.factory("Engineering");

    when(repo.findOptionalById(id)).thenReturn(Optional.of(areaOfExpertise));

    assertThat(service.getById(id)).isEqualTo(areaOfExpertise);
  }

  @Test
  @DisplayName("Should throw when AreaOfExpertise is not found")
  void getByIdNotFound() {
    UUID id = UuidCreator.getTimeOrderedEpoch();

    when(repo.findOptionalById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
  }

  @Test
  @DisplayName("Should throw when stored AreaOfExpertise has field errors")
  void getByIdWithCorruptedDomainObject() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AreaOfExpertise corrupted = AreaOfExpertise.factory("");

    when(repo.findOptionalById(id)).thenReturn(Optional.of(corrupted));

    assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
  }

  @Test
  @DisplayName("Should save AreaOfExpertise")
  void saveSuccess() {
    AreaOfExpertise saved = AreaOfExpertise.factory("Engineering");

    when(repo.existsByName("Engineering")).thenReturn(false);
    when(repo.persist(any())).thenReturn(saved);

    AreaOfExpertise result = service.save(new AreaOfExpertiseCreateCommand("Engineering"));

    assertThat(result).isEqualTo(saved);
    verify(repo).persist(any());
    verify(auditPublisher).fireCreate(AreaOfExpertise.class.getName(), saved.getId());
  }

  @Test
  @DisplayName("Should throw validation exception when saving invalid AreaOfExpertise")
  void saveInvalidData() {
    assertThrows(
        AppValidationException.class, () -> service.save(new AreaOfExpertiseCreateCommand("   ")));
  }

  @Test
  @DisplayName("Should throw duplicate resource when saving existing name")
  void saveDuplicateName() {
    when(repo.existsByName("Engineering")).thenReturn(true);

    assertThrows(
        DuplicateResourceException.class,
        () -> service.save(new AreaOfExpertiseCreateCommand("Engineering")));
  }

  @Test
  @DisplayName("Should update AreaOfExpertise")
  void updateSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    String updatedName = "Updated " + UuidCreator.getTimeOrderedEpoch();
    AreaOfExpertise current = AreaOfExpertise.factory("Original Name");
    AreaOfExpertise updated = current.rename(updatedName);

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current), Optional.of(updated));
    when(repo.existsByName(updatedName)).thenReturn(false);

    AreaOfExpertise result = service.update(id, new AreaOfExpertiseUpdateCommand(updatedName));

    assertThat(result).isEqualTo(updated);
    verify(repo)
        .update(
            argThat(
                persisted ->
                    persisted != null
                        && persisted.getId().equals(updated.getId())
                        && persisted.getName().equals(updated.getName())
                        && persisted
                            .getAuditInfo()
                            .getCreatedAt()
                            .equals(current.getAuditInfo().getCreatedAt())
                        && persisted.getAuditInfo().getUpdatedAt() != null));
    verify(auditPublisher)
        .fireUpdate(
            eq(AreaOfExpertise.class.getName()),
            eq(id),
            eq(current),
            argThat(
                (AreaOfExpertise audited) ->
                    audited != null
                        && audited.getId().equals(updated.getId())
                        && audited.getName().equals(updated.getName())
                        && audited
                            .getAuditInfo()
                            .getCreatedAt()
                            .equals(current.getAuditInfo().getCreatedAt())
                        && audited.getAuditInfo().getUpdatedAt() != null));
  }

  @Test
  @DisplayName("Should skip duplicate check when name does not change")
  void updateSameName() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AreaOfExpertise current = AreaOfExpertise.factory("Same Name");

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current));

    AreaOfExpertise result = service.update(id, new AreaOfExpertiseUpdateCommand("Same Name"));

    assertThat(result).isEqualTo(current);
    verify(repo).update(current);
  }

  @Test
  @DisplayName("Should throw duplicate resource when updating to existing name")
  void updateDuplicateName() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AreaOfExpertise current = AreaOfExpertise.factory("Old Name");

    when(repo.findOptionalById(id)).thenReturn(Optional.of(current));
    when(repo.existsByName("Existing Name")).thenReturn(true);

    assertThrows(
        DuplicateResourceException.class,
        () -> service.update(id, new AreaOfExpertiseUpdateCommand("Existing Name")));
  }

  @Test
  @DisplayName("Should delete AreaOfExpertise")
  void deleteSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();

    when(coursesService.existsAnyByAreaOfExpertiseId(id)).thenReturn(false);
    when(repo.deleteById(id)).thenReturn(true);

    assertThat(service.delete(id)).isTrue();

    verify(projectAreaOfExpertiseService).deleteAllByAreaOfExpertiseId(id);
    verify(auditPublisher).fireDelete(AreaOfExpertise.class.getName(), id);
  }

  @Test
  @DisplayName("Should return false when deleting null or missing AreaOfExpertise")
  void deleteNullOrMissing() {
    UUID id = UuidCreator.getTimeOrderedEpoch();

    when(coursesService.existsAnyByAreaOfExpertiseId(id)).thenReturn(false);
    when(repo.deleteById(id)).thenReturn(false);

    assertThat(service.delete(null)).isFalse();
    assertThat(service.delete(id)).isFalse();
  }

  @Test
  @DisplayName("Should throw when deleting AreaOfExpertise with active courses")
  void deleteWithCourses() {
    UUID id = UuidCreator.getTimeOrderedEpoch();

    when(coursesService.existsAnyByAreaOfExpertiseId(id)).thenReturn(true);

    assertThrows(BusinessRuleException.class, () -> service.delete(id));
  }
}
