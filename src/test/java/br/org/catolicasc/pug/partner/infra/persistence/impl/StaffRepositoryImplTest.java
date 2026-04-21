package br.org.catolicasc.pug.partner.infra.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffRepositoryImplTest {

  @Inject StaffRepositoryImpl repository;
  @Inject TestDataFactory factory;

  private Account account;
  private Entity entity;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.PARTNER);
    entity = factory.createEntity(factory.getAnyCity());
  }

  @Test
  @Transactional
  @DisplayName("Should persist and find Staff")
  void shouldPersistAndFind() {
    Staff staff = factory.createStaff(account, entity);

    Optional<Staff> found = repository.findOptionalByAccountId(account.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getEntityId()).isEqualTo(entity.getId());
  }
}
