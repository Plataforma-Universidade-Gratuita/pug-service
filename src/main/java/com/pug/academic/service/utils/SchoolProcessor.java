package com.pug.academic.service.utils;

import com.pug.academic.domain.School;
import com.pug.shared.utils.StringUtils;

/** Utility class for processing School DTO inputs. */
public class SchoolProcessor {

  /**
   * Helper method to process DTO input and build a new School domain object.
   *
   * @param name The name from DTO.
   * @return The constructed School domain object (may contain errors).
   */
  public static School processCreateInput(String name) {
    return School.factory(name);
  }

  /**
   * Helper method to process DTO input and update an existing School domain object.
   *
   * @param existingSchool The existing school to be updated.
   * @param name The name from DTO (can be null for no change).
   * @return The updated School domain object (may contain errors).
   */
  public static School processUpdateInput(School existingSchool, String name) {
    School updatedSchool = existingSchool;

    if (StringUtils.isNotEmpty(name)) {
      updatedSchool = updatedSchool.changeName(name);
    }

    return updatedSchool;
  }
}
