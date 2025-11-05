package com.pug.academic.infra.persistence;

import com.pug.academic.domain.StudentsRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class StudentsRepositoryImpl
    implements StudentsRepository, PanacheRepositoryBase<StudentsEntity, UUID> {
  @Override
  public void persist(StudentsEntity student) {
    persistAndFlush(student);
  }
}
