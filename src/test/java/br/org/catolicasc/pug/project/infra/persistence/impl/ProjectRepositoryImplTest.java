package br.org.catolicasc.pug.project.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
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
class ProjectRepositoryImplTest {

  @Inject ProjectRepositoryImpl repository;
  @Inject TestDataFactory factory;

  private Entity entity;
  private Account account;

  @BeforeEach
  void setup() {
    entity = factory.createEntity(factory.getAnyCity());
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.PARTNER);
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Project")
  void shouldPersistAndFind() {
    Project project = factory.createProject(entity, account);

    var found = repository.findOptionalById(project.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(project.getName());
  }
}
