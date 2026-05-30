package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.helpers.TestBrazilianIdentifierGenerator;
import br.org.catolicasc.pug.identity.infra.read.UsersQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.UserView;
import br.org.catolicasc.pug.identity.service.dtos.users.UserComplexSearchCriteria;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("UsersReadServiceImpl Coverage")
class UsersReadServiceImplTest {

  @Inject UsersReadServiceImpl service;
  @InjectMock UsersQueries queries;

  @Test
  @DisplayName("Should return user view by ID")
  void getByIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    String cpf = TestBrazilianIdentifierGenerator.generateValidCpf();
    UserView view = new UserView(id, cpf, "Test User", null, null);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound for unknown ID")
  void getByIdNotFound() {
    when(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).thenReturn(Optional.empty());
    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewById(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all users")
  void listAll() {
    when(queries.listAllUsers())
        .thenReturn(
            List.of(new UserView(UuidCreator.getTimeOrderedEpoch(), "111", "User", null, null)));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list users by IDs")
  void listAllByIds() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id)))
        .thenReturn(List.of(new UserView(id, "111", "User", null, null)));

    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list when provided IDs are null or empty")
  void listByIdsInvalid() {
    assertThat(service.listViewsByIds(null)).isEmpty();
    assertThat(service.listViewsByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should execute paginated user search")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 10);
    PageResult<UserView> pageResult =
        new PageResult<>(
            List.of(new UserView(UuidCreator.getTimeOrderedEpoch(), "111", "Ana", null, null)),
            0,
            10,
            1,
            1);
    when(queries.search(any(), any())).thenReturn(pageResult);

    assertThat(service.search(pageQuery, new UserComplexSearchCriteria("111", null, null, "Ana")))
        .isEqualTo(pageResult);
  }
}
