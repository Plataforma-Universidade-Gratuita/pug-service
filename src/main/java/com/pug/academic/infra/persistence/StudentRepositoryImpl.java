package com.pug.academic.infra.persistence;

import com.pug.academic.domain.StudentRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class StudentRepositoryImpl
    implements StudentRepository, PanacheRepositoryBase<StudentEntity, UUID> {
  @Override
  public void persist(StudentEntity student) {
    persistAndFlush(student);
  }
}
