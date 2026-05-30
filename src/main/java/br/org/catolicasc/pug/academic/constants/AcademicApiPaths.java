package br.org.catolicasc.pug.academic.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the academic module.
 *
 * <p>These constants define the public REST contract for areas of expertise, courses, former
 * students, and academic school-to-project navigation under the versioned API namespace.
 */
public final class AcademicApiPaths {

  /** Root collection endpoint for academic areas of expertise. */
  public static final String AREAS_OF_EXPERTISE = "/v1/academic/areas-of-expertise";

  /** Nested project association endpoint for a specific academic area of expertise. */
  public static final String AREAS_OF_EXPERTISE_PROJECTS =
      "/v1/academic/areas-of-expertise/{schoolId}/projects";

  /** Root collection endpoint for academic courses. */
  public static final String COURSES = "/v1/academic/courses";

  /** Root collection endpoint for academic former students. */
  public static final String FORMER_STUDENTS = "/v1/academic/former-students";

  /** Private constructor to prevent instantiation. */
  private AcademicApiPaths() {}
}

