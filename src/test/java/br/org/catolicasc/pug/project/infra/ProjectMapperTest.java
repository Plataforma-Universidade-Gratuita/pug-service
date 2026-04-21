package br.org.catolicasc.pug.project.infra;

import static org.assertj.core.api.Assertions.assertThat;

import br.org.catolicasc.pug.helpers.builders.ProjectBuilder;
import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.infra.persistence.ProjectEntity;
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
}
