package com.pug.project.infra.persistence;

import com.pug.project.domain.Project;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProjectRepository implements PanacheRepositoryBase<Project, UUID> {}
