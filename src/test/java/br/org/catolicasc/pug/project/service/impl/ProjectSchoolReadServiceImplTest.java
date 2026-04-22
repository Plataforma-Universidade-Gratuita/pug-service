package br.org.catolicasc.pug.project.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.org.catolicasc.pug.academic.infra.read.SchoolQueries;
import br.org.catolicasc.pug.academic.infra.read.dtos.SchoolView;
import br.org.catolicasc.pug.project.domain.ProjectSchoolRepository;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.read.ProjectQueries;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
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
@DisplayName("ProjectSchoolReadServiceImpl Coverage")
class ProjectSchoolReadServiceImplTest {

  @Inject ProjectSchoolReadServiceImpl service;
  @InjectMock ProjectSchoolRepository associationRepo;
  @InjectMock SchoolQueries schoolQueries;
  @InjectMock ProjectQueries projectQueries;

  @Test
  @DisplayName("Should list schools by project ID")
  void listAllSchoolsByProjectId() {
    UUID projectId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(associationRepo.findAllSchoolIdsByProjectId(projectId)).thenReturn(Set.of(schoolId));
    when(schoolQueries.listByIds(any()))
        .thenReturn(List.of(new SchoolView(schoolId, "School", now, now)));

    Set<SchoolView> result = service.listAllSchoolsByProjectId(projectId);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty set for null project ID")
  void listAllSchoolsByProjectIdNull() {
    assertThat(service.listAllSchoolsByProjectId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty set when no associations found for project")
  void listAllSchoolsByProjectIdNoAssociations() {
    UUID projectId = UUID.randomUUID();
    when(associationRepo.findAllSchoolIdsByProjectId(projectId)).thenReturn(Set.of());

    assertThat(service.listAllSchoolsByProjectId(projectId)).isEmpty();
  }

  @Test
  @DisplayName("Should list projects by school ID")
  void listAllProjectsBySchoolId() {
    UUID schoolId = UUID.randomUUID();
    UUID projectId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    when(associationRepo.findAllProjectIdsBySchoolId(schoolId)).thenReturn(Set.of(projectId));
    when(projectQueries.listByIds(any()))
        .thenReturn(
            List.of(
                new ProjectView(
                    projectId,
                    "Project",
                    UUID.randomUUID(),
                    "desc",
                    UUID.randomUUID(),
                    10,
                    new BigDecimal("40"),
                    BigDecimal.ZERO,
                    ProjectStatus.PLANNED,
                    null,
                    now,
                    now)));

    Set<ProjectView> result = service.listAllProjectsBySchoolId(schoolId);

    assertThat(result).hasSize(1);
  }

  @Test
  @DisplayName("Should return empty set for null school ID")
  void listAllProjectsBySchoolIdNull() {
    assertThat(service.listAllProjectsBySchoolId(null)).isEmpty();
  }

  @Test
  @DisplayName("Should return empty set when no associations found for school")
  void listAllProjectsBySchoolIdNoAssociations() {
    UUID schoolId = UUID.randomUUID();
    when(associationRepo.findAllProjectIdsBySchoolId(schoolId)).thenReturn(Set.of());

    assertThat(service.listAllProjectsBySchoolId(schoolId)).isEmpty();
  }
}
