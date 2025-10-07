package com.pug.TODO.hours.infra.persistence;

import com.pug.TODO.hours.domain.StudentCounterpartHours;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StudentCounterpartHoursRepository
    implements PanacheRepository<StudentCounterpartHours> {}
