package com.pug.academic.usecase.course.update;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
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
public class UpdateCourseHandler {

  @Inject CourseRepository courseRepo;
  @Inject FieldOfStudyRepository fieldRepo;
  @Inject Validator validator;

  @Transactional
  public UUID handle(UpdateCourseCommand cmd) {
    var v = validator.validate(cmd);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);

    UUID id = cmd.id();
    String name = cmd.name().trim().toLowerCase(Locale.ROOT);

    Course course =
        courseRepo.findByIdOptional(id).orElseThrow(() -> new CourseNotFoundException(id));

    if (courseRepo.existsByNameForAnother(name, id)) throw new DuplicateCourseNameException(name);

    var field =
        fieldRepo
            .findByIdOptional(cmd.fieldId())
            .orElseThrow(() -> new FieldOfStudyNotFoundException(cmd.fieldId()));

    course.setName(name);
    course.setField(field);
    courseRepo.flush();
    return course.getId();
  }
}
