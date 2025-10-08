package com.pug.academic.usecase.course.create;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.exceptions.DuplicateCourseNameException;
import com.pug.academic.domain.exceptions.FieldOfStudyNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.academic.infra.persistence.FieldOfStudyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
public class CreateCourseHandler {

  @Inject CourseRepository courseRepo;
  @Inject FieldOfStudyRepository fieldRepo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(CreateCourseCommand cmd) {
    var vCmd = validator.validate(cmd);
    if (!vCmd.isEmpty()) throw new ConstraintViolationException(vCmd);

    String name = cmd.name().trim().toLowerCase(Locale.ROOT);

    if (courseRepo.existsByName(name)) throw new DuplicateCourseNameException(name);

    var field =
        fieldRepo
            .findByIdOptional(cmd.fieldId())
            .orElseThrow(() -> new FieldOfStudyNotFoundException(cmd.fieldId()));

    var entity = Course.builder().name(name).field(field).build();
    var vEnt = validator.validate(entity);
    if (!vEnt.isEmpty()) throw new ConstraintViolationException(vEnt);

    courseRepo.persist(entity);
    courseRepo.flush();
    return entity.getId();
  }
}
