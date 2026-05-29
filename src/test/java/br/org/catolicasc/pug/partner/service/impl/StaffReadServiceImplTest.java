package br.org.catolicasc.pug.partner.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.partner.infra.read.StaffQueries;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffComplexSearchView;
import br.org.catolicasc.pug.partner.infra.read.dtos.StaffView;
import br.org.catolicasc.pug.partner.service.dtos.StaffComplexSearchCriteria;
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
@DisplayName("StaffReadServiceImpl Coverage")
class StaffReadServiceImplTest {

  @Inject StaffReadServiceImpl service;
  @InjectMock StaffQueries queries;

  @Test
  @DisplayName("Should return staff view by account ID")
  void getByAccountIdSuccess() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    StaffView view =
        new StaffView(null, UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
    when(queries.findOptionalById(accountId)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(accountId)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw ResourceNotFound when staff not found")
  void getByAccountIdNotFound() {
    when(queries.findOptionalById(any())).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> service.getViewByAccountId(UuidCreator.getTimeOrderedEpoch()));
  }

  @Test
  @DisplayName("Should list all staff views")
  void listViews() {
    StaffView view =
        new StaffView(null, UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
    when(queries.listAllStaff()).thenReturn(List.of(view));

    assertThat(service.listViews()).containsExactly(view);
  }

  @Test
  @DisplayName("Should return empty list for null IDs lookup")
  void listViewsByIdsInvalid() {
    assertThat(service.listViewsByIds(null)).isEmpty();
  }

  @Test
  @DisplayName("Should list staff views by IDs successfully")
  void listViewsByIdsSuccess() {
    UUID id = UuidCreator.getTimeOrderedEpoch();
    StaffView view =
        new StaffView(null, UuidCreator.getTimeOrderedEpoch(), UuidCreator.getTimeOrderedEpoch());
    when(queries.listAllByIds(List.of(id))).thenReturn(List.of(view));

    assertThat(service.listViewsByIds(List.of(id))).containsExactly(view);
  }

  @Test
  @DisplayName("Should search staff by complex criteria")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 10);
    StaffComplexSearchCriteria criteria =
        new StaffComplexSearchCriteria("Ana", null, null, null, null, true, List.of());
    PageResult<StaffComplexSearchView> pageResult = new PageResult<>(List.of(), 0, 10, 0, 0);

    when(queries.search(pageQuery, criteria)).thenReturn(pageResult);

    assertThat(service.search(pageQuery, criteria)).isEqualTo(pageResult);
  }
}
