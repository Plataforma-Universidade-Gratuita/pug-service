package com.pug.attendance.infra.persistence;

import com.pug.attendance.domain.ProjectAttendance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectAttendanceRepository implements PanacheRepository<ProjectAttendance> {}
