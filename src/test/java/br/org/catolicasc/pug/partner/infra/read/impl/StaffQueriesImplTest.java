package br.org.catolicasc.pug.partner.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.partner.domain.Staff;
import br.org.catolicasc.pug.partner.service.dtos.staff.StaffComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("StaffQueriesImpl Coverage")
class StaffQueriesImplTest extends BaseSearchTest {

  @Inject StaffQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Account account;
  private Entity entity;
  private Staff staff;
  private User user;

  @BeforeEach
  void setup() throws Exception {
    runInTransaction(
        () -> {
          user = factory.createUser();
          account = factory.createAccount(user, AccountType.PARTNER);
          entity = factory.createEntity(factory.getAnyCity());
          staff = factory.createStaff(account, entity);
        });
  }

  @Test
  @DisplayName("Should find staff by ID successfully")
  void findOptionalByIdSuccess() {
    assertThat(queries.findOptionalById(account.getId())).isPresent();
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @DisplayName("Should list all staff and filter by IDs")
  void listOperations() {
    assertThat(queries.listAllStaff()).isNotEmpty();
    assertThat(queries.listAllByIds(List.of(account.getId()))).hasSize(1);
    assertThat(queries.listAllByIds(List.of(UuidCreator.getTimeOrderedEpoch()))).isEmpty();
  }

  @Test
  @DisplayName("Should search staff by complex criteria")
  void searchSuccess() {
    var result =
        queries.search(
            new PageQuery(0, 10),
            new StaffComplexSearchCriteria(
                user.getName().split(" ")[0],
                user.getCpf().getValue().substring(0, 3),
                account.getEmail().getValue().substring(0, 4),
                null,
                null,
                true,
                List.of(entity.getId())));

    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst().account().id()).isEqualTo(account.getId());
    assertThat(result.content().getFirst().entity().id()).isEqualTo(entity.getId());
  }

  @Test
  @DisplayName("Should search staff by timestamp criteria successfully")
  void searchByTimestampSuccess() {
    OffsetDateTime createdAt = account.getAuditInfo().getCreatedAt();
    var result =
        queries.search(
            new PageQuery(0, 1),
            new StaffComplexSearchCriteria(
                null,
                null,
                null,
                createdAt.minusSeconds(1),
                createdAt.plusSeconds(1),
                true,
                List.of()));

    assertThat(result.content()).anyMatch(v -> v.account().id().equals(account.getId()));
  }

  @Test
  @DisplayName("Should return paginated staff list when search criteria is null")
  void searchWithoutCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);
    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }
}
