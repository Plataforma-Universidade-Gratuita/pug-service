package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.CopyableMapperTest;
import br.org.catolicasc.pug.helpers.builders.domain.ProjectBuilder;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.infra.persistence.ProjectEntity;
import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import com.github.f4b6a3.uuid.UuidCreator;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProjectMapper Tests")
class ProjectMapperTest extends CopyableMapperTest<Project, ProjectEntity> {

  @Override
  protected Project createDomain() {
    return ProjectBuilder.aProject().withName("Mapper Test Project").build();
  }

  @Override
  protected ProjectEntity createEntity() {
    return ProjectEntity.builder().name("Old").status("PLANNED").build();
  }

  @Override
  protected Project mapToDomain(ProjectEntity entity) {
    return ProjectMapper.toDomain(entity);
  }

  @Override
  protected ProjectEntity mapToEntity(Project domain) {
    return ProjectMapper.toEntity(domain);
  }

  @Override
  protected void copy(Project domain, ProjectEntity entity) {
    ProjectMapper.copy(domain, entity);
  }

  @Override
  protected void assertRoundTrip(Project original, Project mapped) {
    assertThat(mapped.getId()).isEqualTo(original.getId());
    assertThat(mapped.getName()).isEqualTo(original.getName());
    assertThat(mapped.getProjectStatus()).isEqualTo(original.getProjectStatus());
    assertThat(mapped.getEntityId()).isEqualTo(original.getEntityId());
    assertThat(mapped.getDescription()).isEqualTo(original.getDescription());
    assertThat(mapped.getProjectInfo().getMaxParticipants())
        .isEqualTo(original.getProjectInfo().getMaxParticipants());
    assertThat(mapped.getProjectInfo().getOfferedHours())
        .isEqualByComparingTo(original.getProjectInfo().getOfferedHours());
    assertThat(mapped.getProjectInfo().getCompletedHours())
        .isEqualByComparingTo(original.getProjectInfo().getCompletedHours());
    assertThat(mapped.getProjectInfo().getCreatedBy())
        .isEqualTo(original.getProjectInfo().getCreatedBy());
    assertThat(mapped.getProjectInfo().getAuditInfo().getCreatedAt())
        .isEqualTo(original.getProjectInfo().getAuditInfo().getCreatedAt());
  }

  @Test
  @DisplayName("copy should update all entity fields from domain")
  void copyShouldUpdateEntityFields() {
    Project project = ProjectBuilder.aProject().withName("Updated Project").build();
    ProjectEntity entity = createEntity();

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
    UUID id = UuidCreator.getTimeOrderedEpoch();
    UUID entityId = UuidCreator.getTimeOrderedEpoch();
    UUID creatorId = UuidCreator.getTimeOrderedEpoch();
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
}
