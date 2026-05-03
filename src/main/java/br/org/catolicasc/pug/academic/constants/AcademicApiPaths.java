package br.org.catolicasc.pug.academic.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/**
 * Centralizes the canonical HTTP route strings exposed by the academic module.
 *
 * <p>These constants define the public REST contract for schools, courses, students, and academic
 * school-to-project navigation under the versioned API namespace.
 */
public final class AcademicApiPaths {

  /** Shared prefix for the current public API version. */
  public static final String VERSION = ApiVersions.V1;

  /** Relative item route fragment used by school, course, and student item endpoints. */
  public static final String ITEM = "/{id}";

  /** Relative self route fragment used by authenticated student endpoints. */
  public static final String SELF = "/me";

  /** Relative bulk-operation route fragment used by student batch creation endpoints. */
  public static final String BULK = "/bulk";

  /** Root collection endpoint for academic schools. */
  public static final String SCHOOLS = VERSION + "/academic/schools";

  /** Item endpoint for a specific academic school. */
  public static final String SCHOOL_BY_ID = SCHOOLS + ITEM;

  /** Nested project association endpoint for a specific academic school. */
  public static final String SCHOOL_PROJECTS = SCHOOLS + "/{schoolId}/projects";

  /** Root collection endpoint for academic courses. */
  public static final String COURSES = VERSION + "/academic/courses";

  /** Item endpoint for a specific academic course. */
  public static final String COURSE_BY_ID = COURSES + ITEM;

  /** Root collection endpoint for academic students. */
  public static final String STUDENTS = VERSION + "/academic/students";

  /** Item endpoint for a specific academic student account. */
  public static final String STUDENT_BY_ID = STUDENTS + ITEM;

  /** Self endpoint for the currently authenticated student. */
  public static final String STUDENT_ME = STUDENTS + SELF;

  /** Bulk creation endpoint for student enrollment imports. */
  public static final String STUDENT_BULK = STUDENTS + BULK;

  /** Private constructor to prevent instantiation. */
  private AcademicApiPaths() {}
}
