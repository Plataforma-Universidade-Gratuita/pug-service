package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.ProjectBuilder;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.persistence.ProjectEntity;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectMapper Tests")
class ProjectMapperTest {

  @Test
  @DisplayName("Should perform round-trip mapping for Project")
  void shouldPerformRoundTrip() {
    Project project = ProjectBuilder.aProject().withName("Mapper Test Project").build();

    ProjectEntity entity = ProjectMapper.toEntity(project);
    Project mapped = ProjectMapper.toDomain(entity);

    assertThat(mapped.getId()).isEqualTo(project.getId());
    assertThat(mapped.getName()).isEqualTo(project.getName());
    assertThat(mapped.getProjectStatus()).isEqualTo(project.getProjectStatus());
  }

  @Test
  @DisplayName("toDomain should return null when entity is null")
  void toDomainShouldReturnNullForNullEntity() {
    assertThat(ProjectMapper.toDomain(null)).isNull();
  }

  @Test
  @DisplayName("toEntity should return null when domain is null")
  void toEntityShouldReturnNullForNullDomain() {
    assertThat(ProjectMapper.toEntity(null)).isNull();
  }

  @Test
  @DisplayName("copy should do nothing when domain is null")
  void copyShouldHandleNullDomain() {
    ProjectEntity entity = ProjectEntity.builder().name("Original").build();
    ProjectMapper.copy(null, entity);
    assertThat(entity.getName()).isEqualTo("Original");
  }

  @Test
  @DisplayName("copy should do nothing when entity is null")
  void copyShouldHandleNullEntity() {
    Project project = ProjectBuilder.aProject().build();
    ProjectMapper.copy(project, null);
  }

  @Test
  @DisplayName("copy should do nothing when both are null")
  void copyShouldHandleBothNull() {
    ProjectMapper.copy(null, null);
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Project project = ProjectBuilder.aProject().withName("Updated Project").build();
    ProjectEntity entity = ProjectEntity.builder().name("Old").status("PLANNED").build();

    ProjectMapper.copy(project, entity);

    assertThat(entity.getName()).isEqualTo("Updated Project");
    assertThat(entity.getEntityId()).isEqualTo(project.getEntityId());
    assertThat(entity.getDescription()).isEqualTo(project.getDescription());
    assertThat(entity.getMaxParticipants())
        .isEqualTo(project.getProjectInfo().getMaxParticipants());
    assertThat(entity.getOfferedHours())
        .isEqualByComparingTo(project.getProjectInfo().getOfferedHours());
    assertThat(entity.getCompletedHours())
        .isEqualByComparingTo(project.getProjectInfo().getCompletedHours());
    assertThat(entity.getStatus()).isEqualTo(project.getProjectStatus().name());
  }

  @Test
  @DisplayName("toView should return null when entity is null")
  void toViewShouldReturnNullForNullEntity() {
    assertThat(ProjectMapper.toView(null)).isNull();
  }

  @Test
  @DisplayName("toView should map all fields correctly")
  void toViewShouldMapAllFields() {
    UUID id = UUID.randomUUID();
    UUID entityId = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    ProjectEntity entity =
        ProjectEntity.builder()
            .id(id)
            .name("Test Project")
            .entityId(entityId)
            .description("desc")
            .createdBy(creatorId)
            .maxParticipants(10)
            .offeredHours(new BigDecimal("40.00"))
            .completedHours(BigDecimal.ZERO)
            .status("PLANNED")
            .closedAt(null)
            .createdAt(now)
            .updatedAt(now)
            .build();

    ProjectView view = ProjectMapper.toView(entity);

    assertThat(view).isNotNull();
    assertThat(view.id()).isEqualTo(id);
    assertThat(view.name()).isEqualTo("Test Project");
    assertThat(view.entityId()).isEqualTo(entityId);
    assertThat(view.description()).isEqualTo("desc");
    assertThat(view.creatorId()).isEqualTo(creatorId);
    assertThat(view.maxParticipants()).isEqualTo(10);
    assertThat(view.offeredHours()).isEqualByComparingTo(new BigDecimal("40.00"));
    assertThat(view.completedHours()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(view.status()).isEqualTo(ProjectStatus.PLANNED);
    assertThat(view.closedAt()).isNull();
  }

  @Test
  @DisplayName("Round-trip should preserve all value objects")
  void roundTripShouldPreserveAllValueObjects() {
    Project project = ProjectBuilder.aProject().build();

    ProjectEntity entity = ProjectMapper.toEntity(project);
    Project mapped = ProjectMapper.toDomain(entity);

    assertThat(mapped.getEntityId()).isEqualTo(project.getEntityId());
    assertThat(mapped.getDescription()).isEqualTo(project.getDescription());
    assertThat(mapped.getProjectInfo().getMaxParticipants())
        .isEqualTo(project.getProjectInfo().getMaxParticipants());
    assertThat(mapped.getProjectInfo().getOfferedHours())
        .isEqualByComparingTo(project.getProjectInfo().getOfferedHours());
    assertThat(mapped.getProjectInfo().getCompletedHours())
        .isEqualByComparingTo(project.getProjectInfo().getCompletedHours());
    assertThat(mapped.getProjectInfo().getCreatedBy())
        .isEqualTo(project.getProjectInfo().getCreatedBy());
    assertThat(mapped.getProjectInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(project.getProjectInfo().getAuditInfo().getCreatedAt());
  }
}
