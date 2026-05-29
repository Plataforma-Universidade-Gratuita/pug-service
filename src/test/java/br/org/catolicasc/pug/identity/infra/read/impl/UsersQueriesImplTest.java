package br.org.catolicasc.pug.identity.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.BaseSearchTest;
import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.User;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.dtos.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UsersQueriesImpl Coverage")
class UsersQueriesImplTest extends BaseSearchTest {

  @Inject UsersQueriesImpl queries;
  @Inject TestDataFactory factory;

  @Test
  @Transactional
  @DisplayName("Should find user by ID successfully")
  void findOptionalByIdSuccess() {
    User user = factory.createUser();
    Optional<UserView> found = queries.findOptionalById(user.getId());
    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(user.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should list all users")
  void list() {
    factory.createUser();
    assertThat(queries.listAllUsers()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list users by IDs successfully")
  void listAllByIdsSuccess() {
    User user = factory.createUser();
    List<UserView> found = queries.listAllByIds(List.of(user.getId()));
    assertThat(found).hasSize(1);
    assertThat(found.getFirst().id()).isEqualTo(user.getId());
  }

  @Test
  @DisplayName("Should return empty when listing users by invalid IDs")
  void listAllByIdsInvalid() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of(UuidCreator.getTimeOrderedEpoch()))).isEmpty();
  }

  @Test
  @DisplayName("Should search users by complex criteria successfully")
  void searchSuccess() throws Exception {
    User[] user = new User[1];
    runInTransaction(() -> user[0] = factory.createUser());

    var result =
        queries.search(
            new PageQuery(0, 10),
            new UserComplexSearchCriteria(
                user[0].getCpf().getValue().substring(0, 3),
                null,
                null,
                user[0].getName().split(" ")[0]));

    assertThat(result.content()).anyMatch(v -> v.id().equals(user[0].getId()));
    assertThat(result.page()).isEqualTo(0);
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @DisplayName("Should search users by timestamp criteria successfully")
  void searchByTimestampSuccess() throws Exception {
    User[] user = new User[1];
    runInTransaction(() -> user[0] = factory.createUser());

    OffsetDateTime createdAt = user[0].getAuditInfo().getCreatedAt();
    var result =
        queries.search(
            new PageQuery(0, 1),
            new UserComplexSearchCriteria(
                null, createdAt.minusSeconds(1), createdAt.plusSeconds(1), null));

    assertThat(result.content()).anyMatch(v -> v.id().equals(user[0].getId()));
  }

  @Test
  @DisplayName("Should return paginated user list when search criteria is null")
  void searchWithoutCriteria() {
    var result = queries.search(new PageQuery(0, 10), null);
    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }
}
