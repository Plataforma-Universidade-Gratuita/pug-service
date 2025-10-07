package com.pug.TODO.enrollment.infra.persistence;

import com.pug.TODO.enrollment.domain.ProjectEnrollment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectEnrollmentRepository implements PanacheRepository<ProjectEnrollment> {}
