package com.pug.project.infra.persistence;

import com.pug.project.domain.ProjectAllocation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectAllocationRepository implements PanacheRepository<ProjectAllocation> {}
