package br.org.catolicasc.pug.partner.service.impl;

import static br.org.catolicasc.pug.helpers.builders.commands.EntityCreateCommandBuilder.anEntityCreateCommand;
import static br.org.catolicasc.pug.helpers.builders.commands.EntityUpdateCommandBuilder.anEntityUpdateCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.geo.domain.City;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.service.ProjectService;
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
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("EntitiesServiceImpl Integration Tests")
class EntitiesServiceImplTest {

  @Inject EntitiesServiceImpl service;
  @Inject TestDataFactory factory;
  @Inject EntityManager em;

  @InjectMock AuditPublisher audit;
  @InjectMock ProjectService projectService;

  @Test
  @Transactional
  @DisplayName("Should save entity successfully")
  void saveSuccess() {
    var cmd = anEntityCreateCommand().withCityId(factory.getAnyCity().getId()).build();

    Entity saved = service.save(cmd);

    assertThat(saved.getName()).isEqualTo(cmd.name());
    verify(audit).fireCreate(Entity.class.getName(), saved.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should throw DuplicateResourceException for same CNPJ")
  void saveDuplicate() {
    Entity created = factory.createEntity(factory.getAnyCity());
    em.flush();

    var cmd =
        anEntityCreateCommand()
            .withCnpj(created.getCnpj().getValue())
            .withCityId(factory.getAnyCity().getId())
            .build();

    assertThrows(DuplicateResourceException.class, () -> service.save(cmd));
  }

  @Test
  @Transactional
  @DisplayName("Should update entity successfully")
  void updateSuccess() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    var cmd = anEntityUpdateCommand().build();

    Entity updated = service.update(entity.getId(), cmd);

    assertThat(updated.getName()).isEqualTo(cmd.name());
    verify(audit).fireUpdate(any(), any(), any(), any());
  }

  @Test
  @Transactional
  @DisplayName("Should delete entity successfully")
  void deleteSuccess() {
    Entity entity = factory.createEntity(factory.getAnyCity());

    when(projectService.existsAnyByEntityId(entity.getId())).thenReturn(false);

    boolean deleted = service.delete(entity.getId());

    assertThat(deleted).isTrue();
    verify(audit).fireDelete(Entity.class.getName(), entity.getId());
  }

  @Test
  @DisplayName("Should check existence by City ID")
  void existsAnyByCityId() {
    City city = factory.getAnyCity();
    factory.createEntity(city);

    boolean exists = service.existsAnyByCityId(city.getId());

    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should return false for null City ID")
  void existsAnyByCityIdNull() {
    boolean exists = service.existsAnyByCityId(null);
    assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("Should return false when deleting with null ID")
  void deleteNullId() {
    boolean result = service.delete(null);
    assertThat(result).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw when deleting entity with projects")
  void deleteWithProjects() {
    Entity entity = factory.createEntity(factory.getAnyCity());

    when(projectService.existsAnyByEntityId(entity.getId())).thenReturn(true);

    assertThrows(BusinessRuleException.class, () -> service.delete(entity.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return false when deleting non-existing entity")
  void deleteNonExisting() {
    UUID randomId = UuidCreator.getTimeOrderedEpoch();

    when(projectService.existsAnyByEntityId(randomId)).thenReturn(false);

    boolean result = service.delete(randomId);

    assertThat(result).isFalse();
  }

  @Test
  @Transactional
  @DisplayName("Should throw validation exception for invalid entity data")
  void saveValidationError() {
    var cmd =
        anEntityCreateCommand()
            .withCnpj("invalid-cnpj")
            .withName("")
            .withCityId(factory.getAnyCity().getId())
            .build();

    assertThrows(AppValidationException.class, () -> service.save(cmd));
  }

  @Test
  @DisplayName("Should throw when updating non-existing entity")
  void updateNotFound() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    var cmd = anEntityUpdateCommand().build();

    assertThrows(ResourceNotFoundException.class, () -> service.update(id, cmd));
  }

  @Test
  @DisplayName("Should throw when entity not found")
  void getByIdNotFound() {
    UUID id = UuidCreator.getTimeOrderedEpoch();

    assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
  }
}
