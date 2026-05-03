package br.org.catolicasc.pug.project.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the project module.
 *
 * <p>These constants define the public REST contract for projects, attendances, enrollments, and
 * project-to-school associations under the versioned API namespace.
 */
public final class ProjectApiPaths {

  /** Root collection endpoint for projects. */
  public static final String PROJECTS = "/v1/projects";

  /** Root collection endpoint for attendance records. */
  public static final String ATTENDANCES = "/v1/projects/attendances";

  /** Nested collection endpoint for project-to-school associations. */
  public static final String PROJECT_SCHOOLS = "/v1/projects/{projectId}/schools";

  /** Private constructor to prevent instantiation. */
  private ProjectApiPaths() {}
}
