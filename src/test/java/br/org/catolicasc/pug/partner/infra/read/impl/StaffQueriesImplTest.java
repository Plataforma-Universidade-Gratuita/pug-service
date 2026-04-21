package br.org.catolicasc.pug.partner.infra.read.impl;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StaffQueriesImplTest {

  @Inject StaffQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Account account;
  private Staff staff;

  @BeforeEach
  void setup() {
    User user = factory.createUser();
    account = factory.createAccount(user, AccountType.PARTNER);
    Entity entity = factory.createEntity(factory.getAnyCity());
    staff = factory.createStaff(account, entity);
  }

  @Test
  @Transactional
  @DisplayName("Should retrieve StaffView including Account and Entity details")
  void shouldFindStaffView() {
    var view = queries.findOptionalById(account.getId());

    assertThat(view).isPresent();
    assertThat(view.get().account().email()).isEqualTo(account.getEmail().getValue());
    assertThat(view.get().entityId()).isEqualTo(staff.getEntityId());
  }
}
