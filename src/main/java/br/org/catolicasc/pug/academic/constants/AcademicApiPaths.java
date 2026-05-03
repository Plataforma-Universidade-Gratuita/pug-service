package br.org.catolicasc.pug.academic.constants;

/** Centralized HTTP path constants for the academic module resources. */
public final class AcademicApiPaths {

  public static final String SCHOOLS = "/v1/academic/schools";
  public static final String COURSES = "/v1/academic/courses";
  public static final String STUDENTS = "/v1/academic/students";

  public static final String BY_ID = "/{id}";
  public static final String ME = "/me";
  public static final String BULK = "/bulk";

  /** Private constructor to prevent instantiation. */
  private AcademicApiPaths() {}
}
