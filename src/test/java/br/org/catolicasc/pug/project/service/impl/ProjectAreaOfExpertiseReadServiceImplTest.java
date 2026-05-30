package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.AreasOfExpertiseQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.AreaOfExpertiseView;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertiseRepository;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import com.github.f4b6a3.uuid.UuidCreator;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisplayName("ProjectAreasOfExpertiseReadServiceImpl Coverage")
class ProjectAreaOfExpertiseReadServiceImplTest {

  @Inject ProjectAreaOfExpertiseReadServiceImpl service;
  @InjectMock ProjectAreaOfExpertiseRepository associationRepo;
  @InjectMock AreasOfExpertiseQueries areaOfExpertiseQueries;
  @InjectMock ProjectQueries projectQueries;

  @Test
  @DisplayName("Should list areaOfExpertises by project ID")
  void listAllAreasOfExpertiseByProjectId() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();

    when(associationRepo.findAllAreaOfExpertiseIdsByProjectId(projectId))
        .thenReturn(Set.of(areaOfExpertiseId));
    when(areaOfExpertiseQueries.listAllByIds(any()))
        .thenReturn(
            List.of(new AreaOfExpertiseView(areaOfExpertiseId, "AreaOfExpertise", now, now)));

    Set<AreaOfExpertiseView> result = service.listAllAreasOfExpertiseByProjectId(projectId);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty set for null project ID")
  void listAllAreasOfExpertiseByProjectIdNull() {
    assertThat(service.listAllAreasOfExpertiseByProjectId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty set when no associations found for project")
  void listAllAreasOfExpertiseByProjectIdNoAssociations() {
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    when(associationRepo.findAllAreaOfExpertiseIdsByProjectId(projectId)).thenReturn(Set.of());

    assertThat(service.listAllAreasOfExpertiseByProjectId(projectId)).isEmpty();
  }

  @Test
  @DisplayName("Should list projects by areaOfExpertise ID")
  void listAllProjectsByAreaOfExpertiseId() {
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    UUID projectId = UuidCreator.getTimeOrderedEpoch();
    OffsetDateTime now = OffsetDateTime.now();

    when(associationRepo.findAllProjectIdsByAreaOfExpertiseId(areaOfExpertiseId))
        .thenReturn(Set.of(projectId));
    when(projectQueries.listAllByIds(any()))
        .thenReturn(
            List.of(
                new ProjectView(
                    projectId,
                    "Project",
                    UuidCreator.getTimeOrderedEpoch(),
                    "Entity Name",
                    "desc",
                    UuidCreator.getTimeOrderedEpoch(),
                    10,
                    new BigDecimal("40"),
                    BigDecimal.ZERO,
                    ProjectStatus.PLANNED,
                    null,
                    now,
                    now)));

    Set<ProjectView> result = service.listAllProjectsByAreaOfExpertiseId(areaOfExpertiseId);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty set for null areaOfExpertise ID")
  void listAllProjectsByAreaOfExpertiseIdNull() {
    assertThat(service.listAllProjectsByAreaOfExpertiseId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty set when no associations found for areaOfExpertise")
  void listAllProjectsByAreaOfExpertiseIdNoAssociations() {
    UUID areaOfExpertiseId = UuidCreator.getTimeOrderedEpoch();
    when(associationRepo.findAllProjectIdsByAreaOfExpertiseId(areaOfExpertiseId))
        .thenReturn(Set.of());

    assertThat(service.listAllProjectsByAreaOfExpertiseId(areaOfExpertiseId)).isEmpty();
  }
}
