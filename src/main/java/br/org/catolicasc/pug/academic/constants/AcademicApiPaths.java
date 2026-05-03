package br.org.catolicasc.pug.academic.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the academic module.
 *
 * <p>These constants define the public REST contract for schools, courses, students, and academic
 * school-to-project navigation under the versioned API namespace.
 */
public final class AcademicApiPaths {

  /** Root collection endpoint for academic schools. */
  public static final String SCHOOLS = "/v1/academic/schools";

  /** Nested project association endpoint for a specific academic school. */
  public static final String SCHOOL_PROJECTS = "/v1/academic/schools/{schoolId}/projects";

  /** Root collection endpoint for academic courses. */
  public static final String COURSES = "/v1/academic/courses";

  /** Root collection endpoint for academic students. */
  public static final String STUDENTS = "/v1/academic/students";

  /** Private constructor to prevent instantiation. */
  private AcademicApiPaths() {}
}
