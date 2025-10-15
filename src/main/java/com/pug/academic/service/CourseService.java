package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.domain.CourseRepository;
import com.pug.shared.application.StringQuery;
import com.pug.shared.application.UuidQuery;
import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CourseService {
  @Inject CourseRepository repo;

  public Optional<Course> getById(@Valid UuidQuery q) {
    return repo.findOptionalById(q.id());
  }

  public Optional<Course> getByName(@Valid StringQuery q) {
    return repo.findByNameIgnoreCase(q.value());
  }

  public Page<Course> listBySchool(@Valid UuidQuery school, PageRequest pr) {
    return repo.listBySchool(school.id(), pr);
  }

  public List<Course> listAllBySchool(UUID schoolId) {
    return repo.listAllBySchool(schoolId);
  }
}
