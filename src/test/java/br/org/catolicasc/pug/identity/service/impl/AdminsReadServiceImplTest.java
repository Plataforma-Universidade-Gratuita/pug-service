package br.org.catolicasc.pug.identity.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.identity.infra.read.AdminsQueries;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminComplexSearchView;
import br.org.catolicasc.pug.identity.infra.read.dtos.AdminView;
import br.org.catolicasc.pug.identity.service.dtos.AdminComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
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
@DisplayName("AdminsReadServiceImpl Coverage")
class AdminsReadServiceImplTest {

  @Inject AdminsReadServiceImpl service;
  @InjectMock AdminsQueries queries;

  @Test
  @DisplayName("Should return admin view successfully")
  void getViewByAccountIdSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    AdminView view = new AdminView(null, null, Campi.JARAGUA_DO_SUL);
    when(queries.findOptionalById(id)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(id)).isEqualTo(view);
    assertThat(service.getViewById(id)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when admin missing")
  void notFound() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.findOptionalById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getViewByAccountId(id));
  }

  @Test
  @DisplayName("Should list all admin views")
  void listViews() {
    when(queries.listAllAdmins())
        .thenReturn(List.of(new AdminView(null, null, Campi.JARAGUA_DO_SUL)));
    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list admin views by ids successfully")
  void listViewsByIdsSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(id)))
        .thenReturn(List.of(new AdminView(null, null, Campi.JARAGUA_DO_SUL)));

    assertThat(service.listViewsByIds(List.of(id))).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list when no ids are provided")
  void listViewsByIdsEmpty() {
    assertThat(service.listViewsByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should delegate paginated search")
  void searchSuccess() {
    PageQuery pageQuery = new PageQuery(0, 10);
    AdminComplexSearchCriteria criteria =
        new AdminComplexSearchCriteria("Admin", null, null, null, null, true);
    PageResult<AdminComplexSearchView> result = new PageResult<>(List.of(), 0, 10, 0, 0);
    when(queries.search(pageQuery, criteria)).thenReturn(result);

    assertThat(service.search(pageQuery, criteria).content()).isEmpty();
    verify(queries).search(pageQuery, criteria);
  }
}
