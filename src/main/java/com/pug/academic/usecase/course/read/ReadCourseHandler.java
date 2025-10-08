package com.pug.academic.usecase.course.read;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.exceptions.CourseNotFoundException;
import com.pug.academic.infra.persistence.CourseRepository;
import com.pug.shared.dtos.ReadByIdQuery;
import com.pug.shared.dtos.ReadByPatternQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.List;

@ApplicationScoped
public class ReadCourseHandler {

  @Inject CourseRepository repo;
  @Inject Validator validator;

  public Course handle(ReadByIdQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    return repo.findByIdOptional(q.id()).orElseThrow(() -> new CourseNotFoundException(q.id()));
  }

  public List<Course> handle(ReadByPatternQuery q) {
    var v = validator.validate(q);
    if (!v.isEmpty()) throw new ConstraintViolationException(v);
    if (q.limit() == null) return repo.listAllSorted();
    if (q.offset() == null || q.offset() == 0) return repo.listByPattern(q.query(), q.limit(), 0);
    return repo.listByPattern(q.query(), q.limit(), q.offset());
  }
}
