package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectQueriesImplTest {

  @Inject ProjectQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Project project;
  private Entity partnerEntity;
  private Account creator;

  @BeforeEach
  void setup() {
    partnerEntity = factory.createEntity(factory.getAnyCity());
    creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(partnerEntity, creator);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve ProjectView by ID")
  void shouldFindById() {
    var view = queries.findOptionalById(project.getId());

    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo(project.getName());
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
    assertThat(queries.findOptionalById(UUID.randomUUID())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all projects")
  void shouldListAllProjects() {
    var list = queries.listAllProjects();
    assertThat(list).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by creator")
  void shouldListByCreatedBy() {
    var list = queries.listByCreatedBy(creator.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.creatorId()).isEqualTo(creator.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null creator ID")
  void shouldReturnEmptyForNullCreator() {
    assertThat(queries.listByCreatedBy(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by entity ID")
  void shouldListByEntityId() {
    var list = queries.listByEntityId(partnerEntity.getId());
    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(v -> assertThat(v.entityId()).isEqualTo(partnerEntity.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null entity ID")
  void shouldReturnEmptyForNullEntityId() {
    assertThat(queries.listByEntityId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by IDs")
  void shouldListByIds() {
    var list = queries.listByIds(List.of(project.getId()));
    assertThat(list).hasSize(1);
    assertThat(list.getFirst().id()).isEqualTo(project.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty list for null IDs")
  void shouldReturnEmptyForNullIds() {
    assertThat(queries.listByIds(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should verify all fields in project view")
  void shouldVerifyAllFieldsInView() {
    var view = queries.findOptionalById(project.getId());

    assertThat(view).isPresent();
    var pv = view.get();
    assertThat(pv.id()).isEqualTo(project.getId());
    assertThat(pv.name()).isEqualTo(project.getName());
    assertThat(pv.entityId()).isEqualTo(project.getEntityId());
    assertThat(pv.description()).isEqualTo(project.getDescription());
    assertThat(pv.creatorId()).isEqualTo(project.getProjectInfo().getCreatedBy());
    assertThat(pv.status()).isEqualTo(project.getProjectStatus());
    assertThat(pv.createdAt()).isNotNull();
    assertThat(pv.updatedAt()).isNotNull();
  }
}
