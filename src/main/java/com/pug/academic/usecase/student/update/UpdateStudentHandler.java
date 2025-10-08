package com.pug.academic.usecase.student.update;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.domain.exceptions.DuplicateAcademicRegistrationException;
import com.pug.academic.domain.exceptions.StudentNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.UUID;

@ApplicationScoped
public class UpdateStudentHandler {

  @Inject StudentRepository repo;
  @Inject CourseRepository courseRepo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(UpdateStudentCommand cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    UUID id = cmd.id();
    String reg = cmd.academicRegistration().trim();

    Student s = repo.findByIdOptional(id).orElseThrow(() -> new StudentNotFoundException(id));

    if (repo.existsByAcademicRegistrationForAnother(reg, id))
      throw new DuplicateAcademicRegistrationException(reg);

    var course =
        courseRepo
            .findByIdOptional(cmd.courseId())
            .orElseThrow(() -> new CourseNotFoundException(cmd.courseId()));

    s.setAcademicRegistration(reg);
    s.setCourse(course);
    repo.flush();
    return s.getId();
  }
}
