package com.pug.TODO.project.infra.persistence;

import com.pug.TODO.project.domain.ProjectLocation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectLocationRepository implements PanacheRepository<ProjectLocation> {}
