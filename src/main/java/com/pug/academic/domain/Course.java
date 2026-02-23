package com.pug.academic.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import com.pug.shared.utils.StringUtils;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Course entity aggregate. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class Course extends DomainError {
  UUID id;
  String name;
  UUID schoolId;
  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private Course(UUID id, String name, UUID schoolId, AuditInfo auditInfo) {
    this.id = id;
    this.name = name;
    this.schoolId = schoolId;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory for new courses.
   *
   * @param name the name of the course
   * @param schoolId the ID of the school
   * @return the created course (may contain errors)
   */
  public static Course factory(String name, UUID schoolId) {
    String trimmedName = StringUtils.trim(name);
    Course course =
        Course.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .name(trimmedName)
            .schoolId(schoolId)
            .auditInfo(AuditInfo.factory())
            .build();

    course.collectValidationProblems();
    return course;
  }

  /**
   * Behavior: change the name of the course.
   *
   * @param newName the new name for the course
   * @return the updated course with the new name
   */
  public Course changeName(String newName) {
    String trimmedName = StringUtils.trim(newName);
    if (name.equals(trimmedName)) {
      return this;
    }
    Course updatedCourse = toBuilder().name(trimmedName).auditInfo(auditInfo.update()).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /**
   * Behavior: move the course to another school.
   *
   * @param newSchoolId the ID of the new school
   * @return the updated course with the new school ID
   */
  public Course moveToSchool(UUID newSchoolId) {
    if (schoolId.equals(newSchoolId)) {
      return this;
    }
    Course updatedCourse = toBuilder().schoolId(newSchoolId).auditInfo(auditInfo.update()).build();
    updatedCourse.collectValidationProblems();
    return updatedCourse;
  }

  /** Collects all validation problems for the Course instance. */
  private void collectValidationProblems() {
    validateIdField(id);
    validateStringField(name, 120L, "name");
    validateForeignKeyField(schoolId, "schoolId");
    if (auditInfo == null) {
      addError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else {
      addErrors(auditInfo.getProblems());
    }
  }
}
