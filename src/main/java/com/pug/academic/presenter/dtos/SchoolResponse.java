package com.pug.academic.presenter.dtos;

import com.pug.academic.domain.School;
import java.util.UUID;

/**
 * Response DTO for School.
 *
 * @param id the school ID
 * @param name the school name
 */
public record SchoolResponse(UUID id, String name) {
  public static SchoolResponse from(School s) {
    return new SchoolResponse(s.getId(), s.getName());
  }
}
