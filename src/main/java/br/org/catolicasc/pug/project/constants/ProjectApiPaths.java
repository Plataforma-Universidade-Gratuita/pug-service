package br.org.catolicasc.pug.project.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/**
 * Centralizes the canonical HTTP route strings exposed by the project module.
 *
 * <p>These constants define the public REST contract for projects, attendances, enrollments, and
 * project-to-school associations under the versioned API namespace.
 */
public final class ProjectApiPaths {

  /** Shared prefix for the current public API version. */
  public static final String VERSION = ApiVersions.V1;

  /** Relative item route fragment used by project and attendance item endpoints. */
  public static final String ITEM = "/{id}";

  /** Relative route fragment used by attendance validation endpoints. */
  public static final String ATTENDANCE_VALIDATE_SEGMENT = "/{id}/validate";

  /** Relative collection route fragment for enrollment queries across projects. */
  public static final String ENROLLMENTS_SEGMENT = "/enrollments";

  /** Relative self route fragment for the current student's enrollment collection. */
  public static final String ENROLLMENTS_ME_SEGMENT = ENROLLMENTS_SEGMENT + "/me";

  /** Relative collection route fragment for project-scoped enrollments. */
  public static final String PROJECT_ENROLLMENTS_SEGMENT = "/{projectId}/enrollments";

  /** Relative item route fragment for a specific project enrollment. */
  public static final String PROJECT_ENROLLMENT_BY_STUDENT_SEGMENT =
      PROJECT_ENROLLMENTS_SEGMENT + "/{studentId}";

  /** Relative self route fragment for the current student's enrollment in a project. */
  public static final String PROJECT_ENROLLMENT_ME_SEGMENT = PROJECT_ENROLLMENTS_SEGMENT + "/me";

  /** Relative item route fragment for a specific project-school association. */
  public static final String PROJECT_SCHOOL_ITEM_SEGMENT = "/{schoolId}";

  /** Root collection endpoint for projects. */
  public static final String PROJECTS = VERSION + "/projects";

  /** Item endpoint for a specific project. */
  public static final String PROJECT_BY_ID = PROJECTS + ITEM;

  /** Root collection endpoint for attendance records. */
  public static final String ATTENDANCES = PROJECTS + "/attendances";

  /** Item endpoint for a specific attendance record. */
  public static final String ATTENDANCE_BY_ID = ATTENDANCES + ITEM;

  /** Validation endpoint for a specific attendance record. */
  public static final String ATTENDANCE_VALIDATE = ATTENDANCES + ATTENDANCE_VALIDATE_SEGMENT;

  /** Collection endpoint for enrollment queries across projects. */
  public static final String ENROLLMENTS = PROJECTS + ENROLLMENTS_SEGMENT;

  /** Self endpoint for the currently authenticated student's enrollment collection. */
  public static final String ENROLLMENTS_ME = PROJECTS + ENROLLMENTS_ME_SEGMENT;

  /** Nested collection endpoint for enrollments within a specific project. */
  public static final String PROJECT_ENROLLMENTS = PROJECTS + PROJECT_ENROLLMENTS_SEGMENT;

  /** Item endpoint for a specific project enrollment. */
  public static final String PROJECT_ENROLLMENT_BY_STUDENT =
      PROJECTS + PROJECT_ENROLLMENT_BY_STUDENT_SEGMENT;

  /** Self endpoint for the current student's enrollment in a specific project. */
  public static final String PROJECT_ENROLLMENT_ME = PROJECTS + PROJECT_ENROLLMENT_ME_SEGMENT;

  /** Nested collection endpoint for project-to-school associations. */
  public static final String PROJECT_SCHOOLS = PROJECTS + "/{projectId}/schools";

  /** Item endpoint for removing a specific school from a project. */
  public static final String PROJECT_SCHOOL_BY_ID = PROJECT_SCHOOLS + PROJECT_SCHOOL_ITEM_SEGMENT;

  /** Private constructor to prevent instantiation. */
  private ProjectApiPaths() {}
}
