package br.org.catolicasc.pug.project.infra.read.impl;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.TestDataFactory;
import br.org.catolicasc.pug.identity.domain.Account;
import br.org.catolicasc.pug.partner.domain.Entity;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectComplexSearchCriteria;
import br.org.catolicasc.pug.shared.domain.enums.AccountType;
import br.org.catolicasc.pug.shared.service.dtos.PageQuery;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectQueriesImpl Coverage")
class ProjectQueriesImplTest {

  @Inject ProjectQueriesImpl queries;
  @Inject TestDataFactory factory;

  private Project project;
  private Entity partnerEntity;
  private Account creator;

  @BeforeEach
  void setup() {
    partnerEntity = factory.createEntity(factory.getAnyCity());
    creator = factory.createAccount(factory.createUser(), AccountType.PARTNER);
    project = factory.createProject(partnerEntity, creator);
  }

  @Test
  @Transactional
  @DisplayName("Should find project view by ID")
  void shouldFindById() {
    var view = queries.findOptionalById(project.getId());

    assertThat(view).isPresent();
    assertThat(view.get().id()).isEqualTo(project.getId());
    assertThat(view.get().entityId()).isEqualTo(partnerEntity.getId());
    assertThat(view.get().entityName()).isEqualTo(partnerEntity.getName());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when ID is null")
  void shouldReturnEmptyForNullId() {
    assertThat(queries.findOptionalById(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when project ID does not exist")
  void shouldReturnEmptyForNonExistingId() {
    assertThat(queries.findOptionalById(UuidCreator.getTimeOrderedEpoch())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by IDs")
  void shouldListByIds() {
    var list = queries.listAllByIds(List.of(project.getId()));

    assertThat(list).hasSize(1);
    assertThat(list.getFirst().id()).isEqualTo(project.getId());
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when listing by null or empty IDs")
  void shouldReturnEmptyForNullOrEmptyIds() {
    assertThat(queries.listAllByIds(null)).isEmpty();
    assertThat(queries.listAllByIds(List.of())).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list all projects")
  void shouldListAll() {
    assertThat(queries.listAll()).isNotEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by creator")
  void shouldListByCreatedBy() {
    var list = queries.listAllByCreatedBy(creator.getId());

    assertThat(list).isNotEmpty();
    assertThat(list).allSatisfy(view -> assertThat(view.creatorId()).isEqualTo(creator.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when creator ID is null")
  void shouldReturnEmptyForNullCreatedBy() {
    assertThat(queries.listAllByCreatedBy(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should list projects by entity")
  void shouldListByEntityId() {
    var list = queries.listAllByEntityId(partnerEntity.getId());

    assertThat(list).isNotEmpty();
    assertThat(list)
        .allSatisfy(view -> assertThat(view.entityId()).isEqualTo(partnerEntity.getId()));
  }

  @Test
  @Transactional
  @DisplayName("Should return empty when entity ID is null")
  void shouldReturnEmptyForNullEntityId() {
    assertThat(queries.listAllByEntityId(null)).isEmpty();
  }

  @Test
  @Transactional
  @DisplayName("Should search projects with all supported filters")
  void shouldSearchByAllFilters() {
    OffsetDateTime createdAt = project.getProjectInfo().getAuditInfo().getCreatedAt();

    var result =
        queries.search(
            new ProjectComplexSearchCriteria(
                project.getName().substring(0, 3),
                List.of(partnerEntity.getId()),
                project.getDescription() == null ? null : project.getDescription().substring(0, 3),
                List.of(creator.getId()),
                createdAt.minusSeconds(1),
                createdAt.plusSeconds(1),
                List.of(ProjectStatus.PLANNED),
                project.getProjectInfo().getOfferedHours().add(BigDecimal.ONE),
                BigDecimal.ZERO),
            new PageQuery(0, 10));

    assertThat(result.content()).anyMatch(view -> view.id().equals(project.getId()));
    assertThat(result.page()).isZero();
    assertThat(result.size()).isEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should search projects without criteria")
  void shouldSearchWithoutCriteria() {
    var result = queries.search(null, new PageQuery(0, 10));

    assertThat(result.content()).hasSizeLessThanOrEqualTo(10);
  }

  @Test
  @Transactional
  @DisplayName("Should use default page query when page query is null")
  void shouldSearchWithNullPageQuery() {
    var result =
        queries.search(
            new ProjectComplexSearchCriteria(null, null, null, null, null, null, null, null, null),
            null);

    assertThat(result.content()).hasSizeLessThanOrEqualTo(25);
  }

  @Test
  @Transactional
  @DisplayName("Should return full result set when page size is the fetch-all sentinel")
  void shouldFetchAllWhenPageSizeIsOne() {
    var result =
        queries.search(
            new ProjectComplexSearchCriteria(
                project.getName().substring(0, 3), null, null, null, null, null, null, null, null),
            new PageQuery(5, 1));

    assertThat(result.page()).isZero();
    assertThat(result.content().size()).isEqualTo(result.totalElements());
    assertThat(result.totalPages()).isLessThanOrEqualTo(1);
  }
}
