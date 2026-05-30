package br.org.catolicasc.pug.academic.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.FormerStudentsQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentComplexSearchView;
import br.org.catolicasc.pug.academic.infra.read.dtos.FormerStudentView;
import br.org.catolicasc.pug.academic.service.dtos.formerstudents.FormerStudentComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import br.org.catolicasc.pug.shared.service.dtos.PageResult;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("FormerStudentsReadServiceImpl Coverage")
class FormerStudentsReadServiceImplTest {

  @Inject FormerStudentsReadServiceImpl service;
  @InjectMock FormerStudentsQueries queries;

  @Test
  @DisplayName("Should return former-student view by account ID")
  void getViewByAccountIdSuccess() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    FormerStudentView view = view(accountId);

    when(queries.findOptionalById(accountId)).thenReturn(Optional.of(view));

    assertThat(service.getViewByAccountId(accountId)).isEqualTo(view);
  }

  @Test
  @DisplayName("Should throw when former-student view is not found")
  void getViewByAccountIdNotFound() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();

    when(queries.findOptionalById(accountId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getViewByAccountId(accountId));
  }

  @Test
  @DisplayName("Should list all views")
  void listViews() {
    when(queries.listAllFormerStudents())
        .thenReturn(List.of(view(UuidCreator.getTimeOrderedEpoch())));

    assertThat(service.listViews()).hasSize(1);
  }

  @Test
  @DisplayName("Should list views by IDs")
  void listViewsByIds() {
    UUID accountId = UuidCreator.getTimeOrderedEpoch();
    when(queries.listAllByIds(List.of(accountId))).thenReturn(List.of(view(accountId)));

    assertThat(service.listViewsByIds(List.of(accountId))).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty list when listing by null or empty IDs")
  void listViewsByIdsEmptyInputs() {
    assertThat(service.listViewsByIds(null)).isEmpty();
    assertThat(service.listViewsByIds(List.of())).isEmpty();
  }

  @Test
  @DisplayName("Should delegate paginated search")
  void search() {
    PageQuery pageQuery = new PageQuery(0, 25);
    FormerStudentComplexSearchCriteria criteria =
        new FormerStudentComplexSearchCriteria(
            null, null, null, null, null, null, null, false, null, null, true, null, null);
    PageResult<FormerStudentComplexSearchView> expected = new PageResult<>(List.of(), 0, 25, 0, 0);

    when(queries.search(pageQuery, criteria)).thenReturn(expected);

    assertThat(service.search(pageQuery, criteria)).isEqualTo(expected);
  }

  private FormerStudentView view(UUID accountId) {
    OffsetDateTime now = OffsetDateTime.now();
    LocalDate today = LocalDate.now();
    return new FormerStudentView(
        accountId,
        "REG123",
        Campi.JOINVILLE,
        UuidCreator.getTimeOrderedEpoch(),
        new BigDecimal("100"),
        BigDecimal.ZERO,
        false,
        today,
        today.plusMonths(6),
        now,
        now);
  }
}
