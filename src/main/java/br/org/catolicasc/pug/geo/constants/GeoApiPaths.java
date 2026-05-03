package br.org.catolicasc.pug.geo.constants;

import br.org.catolicasc.pug.shared.constants.ApiVersions;

/**
 * Centralizes the canonical HTTP route strings exposed by the geo module.
 *
 * <p>These constants define the public REST contract for city lookup and search endpoints under
 * the versioned API namespace.
 */
public final class GeoApiPaths {

  /** Shared prefix for the current public API version. */
  public static final String VERSION = ApiVersions.V1;

  /** Relative item route fragment used by city item endpoints. */
  public static final String ITEM = "/{id}";

  /** Relative route fragment used by city lookup through IBGE codes. */
  public static final String BY_IBGE = "/by-ibge/{ibgeCode}";

  /** Root collection endpoint for cities. */
  public static final String CITIES = VERSION + "/geo/cities";

  /** Item endpoint for a specific city. */
  public static final String CITY_BY_ID = CITIES + ITEM;

  /** Item endpoint for a specific city resolved by IBGE code. */
  public static final String CITY_BY_IBGE = CITIES + BY_IBGE;

  /** Private constructor to prevent instantiation. */
  private GeoApiPaths() {}
}
