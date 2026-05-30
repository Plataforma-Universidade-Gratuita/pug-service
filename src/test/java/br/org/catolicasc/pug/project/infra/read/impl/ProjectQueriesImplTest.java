package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.service.dtos.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
  void shouldFindById() {
    var view = queries.findOptionalById(project.getId());
    assertThat(view).isPresent();
    assertThat(view.get().entityName()).isEqualTo(partnerEntity.getName());
  }

  @Test
  @Transactional
  void shouldListByIds() {
    assertThat(queries.listAllByIds(List.of(project.getId()))).hasSize(1);
  }

  @Test
  @Transactional
  void shouldSearch() {
    var result =
        queries.search(
            new ProjectComplexSearchCriteria(
                project.getName(),
                List.of(partnerEntity.getId()),
                null,
                List.of(creator.getId()),
                null,
                null,
                List.of(project.getProjectStatus()),
                null,
                null),
            new PageQuery(0, 1));

    assertThat(result.content()).isNotEmpty();
  }

  @Test
  @Transactional
  void shouldReturnEmptyForNonExistentId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }
}
