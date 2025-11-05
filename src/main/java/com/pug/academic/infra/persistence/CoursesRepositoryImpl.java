package com.pug.academic.infra.persistence;

import com.pug.academic.domain.CoursesRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class CoursesRepositoryImpl
    implements CoursesRepository, PanacheRepositoryBase<CoursesEntity, UUID> {
  @Override
  public void persist(CoursesEntity course) {
    persistAndFlush(course);
  }
}
