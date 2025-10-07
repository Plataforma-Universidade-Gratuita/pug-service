package com.pug.TODO.attendance.persistence;

import com.pug.TODO.attendance.domain.ProjectAttendance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProjectAttendanceRepository implements PanacheRepository<ProjectAttendance> {}
