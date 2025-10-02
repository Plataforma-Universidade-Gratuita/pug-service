package com.pug.student.infra.persistence;

import com.pug.student.domain.FieldOfStudy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class FieldOfStudyRepository implements PanacheRepository<FieldOfStudy> {}
