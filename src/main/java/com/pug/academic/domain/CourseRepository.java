package com.pug.academic.domain;

import com.pug.shared.infra.persistence.Page;
import com.pug.shared.infra.persistence.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository {
  Course save(Course course);

  Optional<Course> findOptionalById(UUID id);

  Optional<Course> findByNameIgnoreCase(String name);

  Page<Course> listBySchool(UUID schoolId, PageRequest pr);

  List<Course> listAllBySchool(UUID schoolId);
}
