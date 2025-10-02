package com.pug.project.infra.persistence;

import com.pug.project.domain.Project;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class ProjectRepository implements PanacheRepository<Project> {}
