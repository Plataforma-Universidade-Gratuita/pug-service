package br.org.catolicasc.pug.builders;

import br.org.catolicasc.pug.project.domain.Project;
import java.math.BigDecimal;
import java.util.UUID;

public class ProjectBuilder {
  private String name = "Default Project";
  private UUID entityId = UUID.randomUUID();
  private UUID creatorId = UUID.randomUUID();
  private BigDecimal offeredHours = new BigDecimal("40.0");

  private ProjectBuilder() {}

  public static ProjectBuilder aProject() {
    return new ProjectBuilder();
  }

  public ProjectBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public Project build() {
    return Project.factory(
        name, entityId, "Project description", creatorId, 20, offeredHours, BigDecimal.ZERO);
  }
}
