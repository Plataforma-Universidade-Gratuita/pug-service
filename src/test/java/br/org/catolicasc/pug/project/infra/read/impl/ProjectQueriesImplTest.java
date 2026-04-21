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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProjectQueriesImplTest {

  @Inject ProjectQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Project project;

  @BeforeEach
  void setup() {
    Entity entity = factory.createEntity(factory.getAnyCity());
    Account creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(entity, creator);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve ProjectView by ID")
  void shouldFindById() {
    var view = queries.findOptionalById(project.getId());

    assertThat(view).isPresent();
    assertThat(view.get().name()).isEqualTo(project.getName());
  }
}
