package br.org.catolicasc.pug.shared.constants;

/**
 * Centralizes the public API version prefixes shared across modules.
 *
 * <p>These constants are intended for places where the version prefix must be referenced outside of
 * the REST annotation metadata, such as URI construction and cross-module path declarations.
 */
public final class ApiVersions {

  /** Canonical prefix for version 1 of the public HTTP API. */
  public static final String V1 = "/v1";

  /** Private constructor to prevent instantiation. */
  private ApiVersions() {}
}
