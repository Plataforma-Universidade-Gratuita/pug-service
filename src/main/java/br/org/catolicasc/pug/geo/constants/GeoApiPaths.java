package br.org.catolicasc.pug.geo.constants;

/**
 * Centralizes the canonical HTTP route strings exposed by the geo module.
 *
 * <p>These constants define the public REST contract for city lookup and search endpoints under the
 * versioned API namespace.
 */
public final class GeoApiPaths {

  /** Root collection endpoint for cities. */
  public static final String CITIES = "/v1/geo/cities";

  /** Private constructor to prevent instantiation. */
  private GeoApiPaths() {}
}
