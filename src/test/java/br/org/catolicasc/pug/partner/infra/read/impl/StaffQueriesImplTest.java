package br.org.catolicasc.pug.partner.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.persistence.UserEntity;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffQueriesImpl Coverage")
class StaffQueriesImplTest extends BaseSearchTest {

  @Inject StaffQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Account account;
  private Staff staff;
  private User user;

  @BeforeEach
  void setup() throws Exception {
    runInTransaction(
        () -> {
          user = factory.createUser();
          account = factory.createAccount(user, AccountType.PARTNER);
          Entity entity = factory.createEntity(factory.getAnyCity());
          staff = factory.createStaff(account, entity);
        });
  }

  @Test
  @DisplayName("Should find staff by ID, Email, and CPF")
  void findByVariousFilters() {
    assertThat(queries.findOptionalById(account.getId())).isPresent();
    assertThat(queries.findOptionalByEmail(account.getEmail().getValue())).isPresent();
    assertThat(queries.listByCpf(user.getCpf().getValue())).hasSize(1);

    assertThat(queries.findOptionalById(UUID.randomUUID())).isEmpty();
  }

  @Test
  @DisplayName("Should list all staff and by EntityID")
  void listOperations() {
    assertThat(queries.listAllStaff()).isNotEmpty();
    assertThat(queries.listAllByEntityId(staff.getEntityId())).hasSize(1);
  }

  @Test
  @DisplayName("Should search staff by user name")
  void searchByName() throws Exception {
    syncIndex(UserEntity.class);

    List<StaffView> found = queries.searchByName(user.getName().substring(0, 3));
    assertThat(found).anyMatch(v -> v.account().userId().equals(user.getId()));

    assertThat(queries.searchByName("NonExistent")).isEmpty();
  }
}
