package com.pug.hours.infra.persistence;

import com.pug.hours.domain.StudentCounterpartHours;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StudentCounterpartHoursRepository
    implements PanacheRepository<StudentCounterpartHours> {}
