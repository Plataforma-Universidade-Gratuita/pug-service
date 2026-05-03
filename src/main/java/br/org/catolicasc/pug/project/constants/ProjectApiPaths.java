package br.org.catolicasc.pug.project.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/** Centralized HTTP path constants for the project module resources. */
public final class ProjectApiPaths {

  public static final String VERSION = ApiVersions.V1;

  public static final String PROJECTS = "/v1/projects";
  public static final String ATTENDANCES = "/v1/projects/attendances";
  public static final String PROJECT_SCHOOLS = "/v1/projects/{projectId}/schools";
  public static final String SCHOOL_PROJECTS = "/v1/academic/schools/{schoolId}/projects";

  public static final String BY_ID = "/{id}";
  public static final String VALIDATE = "/{id}/validate";
  public static final String SCHOOL_ID = "/{schoolId}";
  public static final String ENROLLMENT_BY_STUDENT = "/{projectId}/enrollments/{studentId}";
  public static final String ENROLLMENT_ME_IN_PROJECT = "/{projectId}/enrollments/me";
  public static final String ENROLLMENTS = "/enrollments";
  public static final String ENROLLMENTS_ME = "/enrollments/me";
  public static final String ENROLLMENTS_BY_PROJECT = "/{projectId}/enrollments";

  /** Private constructor to prevent instantiation. */
  private ProjectApiPaths() {}
}
