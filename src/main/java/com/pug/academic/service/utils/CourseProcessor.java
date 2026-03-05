package com.pug.academic.service.utils;

import com.pug.academic.domain.Course;
import com.pug.shared.utils.StringUtils;

import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw DTO command data into
 * pure {@link Course} Domain Aggregates.
 * <p>
 * This processor centralizes the orchestration of domain factory methods and state-mutation
 * behaviors, ensuring that complex initialization or update logic does not pollute the
 * application service layer.
 */
public class CourseProcessor {

  /**
   * Processes raw creation inputs and constructs a new {@link Course} domain aggregate.
   * <p>
   * <b>Note:</b> The returned {@link Course} object may contain accumulated domain validation
   * failures. The caller is responsible for checking {@link Course#hasFieldErrors()} and
   * handling them appropriately.
   *
   * @param name     the raw name of the course requested for creation
   * @param schoolId the unique identifier of the school offering the course
   * @return a fully instantiated {@link Course} domain aggregate, potentially containing validation errors
   */
  public static Course processCreateInput(String name, UUID schoolId) {
    return Course.factory(name, schoolId);
  }

  /**
   * Processes raw update inputs and conditionally mutates the state of an existing {@link Course}.
   * <p>
   * This method applies partial updates. Only fields that are explicitly provided
   * (i.e., not null and not empty) will trigger a state mutation via the aggregate's domain behaviors.
   * <p>
   * Because domain entities in this system are modeled as immutable records, this method
   * returns a <i>new</i> instance of the {@link Course} reflecting the applied changes.
   *
   * @param existingCourse the current, reconstituted {@link Course} aggregate from the repository
   * @param name           the proposed new course name, or {@code null}/empty to skip updating
   * @param schoolId       the proposed new school ID, or {@code null} to skip updating
   * @return a new {@link Course} domain aggregate reflecting the requested updates, potentially containing validation errors
   */
  public static Course processUpdateInput(Course existingCourse, String name, UUID schoolId) {
    Course updatedCourse = existingCourse;

    if (StringUtils.isNotEmpty(name)) {
      updatedCourse = updatedCourse.rename(name);
    }

    if (schoolId != null) {
      updatedCourse = updatedCourse.moveToSchool(schoolId);
    }

    return updatedCourse;
  }
}