package com.pug.enrollment.infra.persistence;

import com.pug.enrollment.domain.ProjectEnrollment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class ProjectEnrollmentRepository implements PanacheRepository<ProjectEnrollment> {}
